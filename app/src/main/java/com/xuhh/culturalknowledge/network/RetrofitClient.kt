package com.xuhh.culturalknowledge.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * Retrofit网络客户端
 * 负责配置和创建网络请求客户端
 * 
 * 主要功能：
 * 1. 配置HTTPS证书信任
 * 2. 设置网络请求拦截器
 * 3. 创建Retrofit实例
 * 4. 提供API服务接口实例
 */
object RetrofitClient {
    /** API基础URL */
    private const val BASE_URL = "https://shiqu.zhilehuo.com/"
    /** 用户会话Cookie */
    private const val COOKIE = "sid=i5VMMK2c7EEm5qK597kJeDqrel7NKCRqSQRGQn8mJBs="

    /**
     * 信任所有证书的TrustManager
     * 用于处理自签名证书
     */
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    /**
     * SSL上下文
     * 配置SSL连接参数
     */
    private val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, java.security.SecureRandom())
    }

    /**
     * OkHttp客户端
     * 配置网络请求客户端
     * 
     * 配置项：
     * 1. 添加Cookie请求头
     * 2. 添加日志拦截器
     * 3. 配置SSL证书
     * 4. 设置超时时间
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Cookie", COOKIE)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Retrofit实例
     * 配置网络请求框架
     * 
     * 配置项：
     * 1. 设置基础URL
     * 2. 配置OkHttp客户端
     * 3. 添加Gson转换器
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** API服务接口实例 */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 