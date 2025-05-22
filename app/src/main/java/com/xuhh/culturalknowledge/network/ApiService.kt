package com.xuhh.culturalknowledge.network

import com.xuhh.culturalknowledge.model.ArticleDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API服务接口
 * 定义与服务器通信的网络请求方法
 */
interface ApiService {
    /**
     * 获取文章详情
     * 
     * @param aid 文章ID
     * @return ArticleDetailResponse 文章详情响应数据
     */
    @GET("knowledge/article/getArticleDetail")
    suspend fun getArticleDetail(@Query("aid") aid: Int): ArticleDetailResponse
} 