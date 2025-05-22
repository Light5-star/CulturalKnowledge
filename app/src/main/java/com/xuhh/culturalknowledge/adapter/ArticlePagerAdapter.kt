package com.xuhh.culturalknowledge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xuhh.culturalknowledge.R
import com.xuhh.culturalknowledge.databinding.ItemArticlePageBinding
import com.xuhh.culturalknowledge.model.ContentItem
import com.xuhh.culturalknowledge.widget.VolumeControlView

/**
 * 文章页面适配器
 * 负责管理文章内容的显示和音频控制
 * 
 * 主要功能：
 * 1. 显示文章页面的图片和文本内容
 * 2. 处理音频播放控制（播放、暂停、重播）
 * 3. 管理页面切换时的音频播放状态
 */
class ArticlePagerAdapter(
    private val items: List<ContentItem>,
    private val onPlayAudio: (String) -> Unit,
    private val onVolumeChange: (Float) -> Unit
) : RecyclerView.Adapter<ArticlePagerAdapter.PageViewHolder>() {

    /** 当前页面位置 */
    private var currentPosition = -1
    /** 音频播放状态 */
    private var isPlaying = true
    /** 当前音量值 */
    private var currentVolume = 1.0f
    /** 当前显示的音量控制视图 */
    private var currentVolumeControlView: VolumeControlView? = null

    /**
     * 创建ViewHolder
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return PageViewHolder 页面视图持有者
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemArticlePageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PageViewHolder(binding)
    }

    /**
     * 绑定ViewHolder
     * @param holder 页面视图持有者
     * @param position 位置
     */
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    /**
     * 获取项目数量
     * @return Int 文章内容列表大小
     */
    override fun getItemCount(): Int = items.size

    /**
     * 处理页面选择事件
     * 在页面切换时触发音频播放
     * @param position 选中的页面位置
     */
    fun onPageSelected(position: Int) {
        if (position != currentPosition) {
            currentPosition = position
            isPlaying = true
            items[position].audioUrl?.let { url ->
                onPlayAudio(url)
            }
            // 隐藏当前显示的音量控制视图
            currentVolumeControlView?.visibility = android.view.View.GONE
            currentVolumeControlView = null
        }
    }

    /**
     * 更新当前音量
     * @param volume 新的音量值
     */
    fun updateVolume(volume: Float) {
        currentVolume = volume
        // 通知所有可见的ViewHolder更新音量控制视图
        for (i in 0 until itemCount) {
            val holder = (items[i] as? PageViewHolder)
            holder?.updateVolumeControl(volume)
        }
    }

    /**
     * 页面视图持有者
     * 负责管理单个页面的视图和交互
     */
    inner class PageViewHolder(
        private val binding: ItemArticlePageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /** 音量控制视图 */
        private var volumeControlView: VolumeControlView? = null

        /**
         * 绑定页面数据
         * @param item 内容项
         * @param position 位置
         */
        fun bind(item: ContentItem, position: Int) {
            // 加载图片
            val imageUrl = if (item.imgUrl.startsWith("http")) {
                item.imgUrl
            } else {
                "https://test.shiqu.zhilehuo.com${item.imgUrl}"
            }
            
            Glide.with(binding.root)
                .load(imageUrl)
                .centerCrop()
                .into(binding.ivPage)

            // 设置内容
            binding.tvContent.text = item.sentence

            // 设置控制按钮点击事件
            binding.btnSound.setOnClickListener {
                showVolumeControl()
            }

            binding.btnReplay.setOnClickListener {
                isPlaying = true
                binding.btnPause.setText(R.string.pause)
                onPlayAudio(item.audioUrl)
            }

            binding.btnPause.setOnClickListener {
                if (isPlaying) {
                    // 暂停播放
                    isPlaying = false
                    binding.btnPause.setText(R.string.play)
                    onPlayAudio("pause")
                } else {
                    // 继续播放
                    isPlaying = true
                    binding.btnPause.setText(R.string.pause)
                    onPlayAudio("resume")
                }
            }
        }

        /**
         * 更新音量控制视图
         * @param volume 新的音量值
         */
        fun updateVolumeControl(volume: Float) {
            volumeControlView?.setProgress((volume * 100).toInt())
        }

        /**
         * 显示音量调节控件
         */
        private fun showVolumeControl() {
            if (volumeControlView == null) {
                // 先移除可能存在的旧视图
                (binding.root as? ConstraintLayout)?.findViewById<VolumeControlView>(R.id.volume_control)?.let {
                    (binding.root as? ConstraintLayout)?.removeView(it)
                }

                volumeControlView = VolumeControlView(binding.root.context).apply {
                    id = R.id.volume_control
                    layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        // 设置位置在音量按钮正上方
                        bottomToTop = binding.btnSound.id
                        startToStart = binding.btnSound.id
                        endToEnd = binding.btnSound.id
                        bottomMargin = 20
                        marginStart = 16
                    }
                    // 设置初始音量
                    setProgress((currentVolume * 100).toInt())
                    setOnVolumeChangeListener { progress ->
                        // 将进度值转换为0.0-1.0的音量值
                        val volume = progress / 100f
                        // 通知Activity更新音量
                        onVolumeChange(volume)
                    }
                }
                (binding.root as? ConstraintLayout)?.addView(volumeControlView)
            }
            
            // 如果当前页面的音量控制视图是当前显示的视图
            if (volumeControlView == currentVolumeControlView) {
                // 隐藏音量控制视图
                volumeControlView?.visibility = android.view.View.GONE
                currentVolumeControlView = null
            } else {
                // 隐藏其他页面的音量控制视图
                currentVolumeControlView?.visibility = android.view.View.GONE
                // 显示当前页面的音量控制视图
                volumeControlView?.visibility = android.view.View.VISIBLE
                currentVolumeControlView = volumeControlView
            }
        }
    }
} 