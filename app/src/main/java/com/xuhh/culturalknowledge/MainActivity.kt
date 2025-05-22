package com.xuhh.culturalknowledge

import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.xuhh.culturalknowledge.adapter.ArticlePagerAdapter
import com.xuhh.culturalknowledge.databinding.ActivityMainBinding
import com.xuhh.culturalknowledge.model.ArticleDetail
import com.xuhh.culturalknowledge.network.RetrofitClient
import com.xuhh.culturalknowledge.ui.base.BaseActivity
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper

/**
 * 主活动类
 * 负责展示文章内容、处理音频播放和页面切换
 * 
 * 主要功能：
 * 1. 加载并显示文章内容
 * 2. 管理文章页面的切换
 * 3. 控制音频的播放、暂停和恢复
 * 4. 显示阅读进度
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    /** 音频播放器实例 */
    private var mediaPlayer: MediaPlayer? = null
    /** 当前加载的文章详情 */
    private var currentArticle: ArticleDetail? = null
    /** 文章页面适配器 */
    private lateinit var pagerAdapter: ArticlePagerAdapter
    /** 当前音量 */
    private var currentVolume = 1.0f
    /** 进度更新Handler */
    private val progressHandler = Handler(Looper.getMainLooper())
    /** 进度更新Runnable */
    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val currentPosition = player.currentPosition
                    // 检查是否播放完成
                    if (currentPosition >= player.duration) {
                        // 播放完成，停止更新
                        progressHandler.removeCallbacks(this)
                        // 重置高亮
                        pagerAdapter.updateTextHighlight(-1)
                        return
                    }
                    // 通知适配器更新文字高亮
                    pagerAdapter.updateTextHighlight(currentPosition)
                    // 继续监听进度，使用更短的延迟以提高更新频率
                    progressHandler.postDelayed(this, 50) // 每50毫秒更新一次，确保平滑的高亮效果
                }
            }
        }
    }

    /**
     * 初始化视图绑定
     * @return ActivityMainBinding 视图绑定实例
     */
    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    /**
     * 初始化视图
     * 设置ViewPager并加载文章内容
     */
    override fun initView() {
        super.initView()
        setupViewPager()
        loadArticle()
    }

    /**
     * 设置ViewPager
     * 初始化适配器并设置页面切换监听
     */
    private fun setupViewPager() {
        pagerAdapter = ArticlePagerAdapter(
            emptyList(),
            { audioUrl -> playAudio(audioUrl) },
            { volume -> updateVolume(volume) }
        )
        mBinding.viewPager.adapter = pagerAdapter

        // 监听页面切换
        mBinding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateProgress(position + 1, pagerAdapter.itemCount)
                // 停止当前音频播放
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                // 通知适配器页面已切换
                pagerAdapter.onPageSelected(position)
            }
        })
    }

    /**
     * 更新音量
     * @param volume 新的音量值（0.0-1.0）
     */
    private fun updateVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer?.setVolume(currentVolume, currentVolume)
        // 通知适配器更新音量控制视图
        pagerAdapter.updateVolume(volume)
    }

    /**
     * 加载文章内容
     * 从服务器获取文章详情并更新UI
     */
    private fun loadArticle() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getArticleDetail(6)
                if (response.code == 0) {
                    currentArticle = response.data
                    updateUI(response.data)
                } else {
                    Toast.makeText(this@MainActivity, response.msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 更新UI显示
     * @param article 文章详情数据
     */
    private fun updateUI(article: ArticleDetail) {
        // 设置标题
        mBinding.tvTitle.text = article.title

        // 更新ViewPager数据
        pagerAdapter = ArticlePagerAdapter(
            article.contentList,
            { audioUrl -> playAudio(audioUrl) },
            { volume -> updateVolume(volume) }
        )
        mBinding.viewPager.adapter = pagerAdapter

        // 更新进度
        updateProgress(1, article.contentList.size)
        
        // 确保ViewPager已经准备好后再播放第一页音频
        mBinding.viewPager.post {
            pagerAdapter.onPageSelected(0)
        }
    }

    /**
     * 更新阅读进度
     * @param current 当前页码
     * @param total 总页数
     */
    private fun updateProgress(current: Int, total: Int) {
        mBinding.tvProgress.text = "$current/$total"
        mBinding.progressBar.max = total
        mBinding.progressBar.progress = current
    }

    /**
     * 播放音频
     * 处理音频的播放、暂停和恢复操作
     * @param audioUrl 音频URL或控制命令（"pause"/"resume"）
     */
    private fun playAudio(audioUrl: String?) {
        if (audioUrl == null) {
            Toast.makeText(this, "没有可播放的音频", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            when {
                audioUrl == "pause" -> {
                    mediaPlayer?.pause()
                    // 停止进度更新
                    progressHandler.removeCallbacks(progressRunnable)
                }
                audioUrl == "resume" -> {
                    mediaPlayer?.start()
                    // 开始进度更新
                    progressHandler.post(progressRunnable)
                }
                audioUrl.startsWith("volume:") -> {
                    // 处理音量调节
                    currentVolume = audioUrl.substringAfter("volume:").toFloatOrNull() ?: 1.0f
                    mediaPlayer?.setVolume(currentVolume, currentVolume)
                }
                else -> {
                    // 停止当前播放
                    mediaPlayer?.release()
                    
                    // 创建新的MediaPlayer
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioUrl)
                        prepare()
                        setVolume(currentVolume, currentVolume)  // 设置当前音量
                        start()
                        // 开始进度更新
                        progressHandler.post(progressRunnable)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "播放失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 活动销毁时释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        // 停止进度更新
        progressHandler.removeCallbacks(progressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}