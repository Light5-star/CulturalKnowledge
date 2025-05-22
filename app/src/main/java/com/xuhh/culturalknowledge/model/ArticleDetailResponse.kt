package com.xuhh.culturalknowledge.model

import com.google.gson.annotations.SerializedName

/**
 * 文章详情响应数据类
 * 用于解析服务器返回的文章详情数据
 *
 * @property code 响应状态码，0表示成功
 * @property msg 响应消息
 * @property data 文章详情数据
 */
data class ArticleDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val data: ArticleDetail
)

/**
 * 文章详情数据类
 * 包含文章的基本信息和内容列表
 *
 * @property id 文章ID
 * @property cover 文章封面图片URL
 * @property title 文章标题
 * @property wordNum 文章字数
 * @property readCount 阅读次数
 * @property bgmUrl 背景音乐URL
 * @property typeId 文章类型ID
 * @property typeName 文章类型名称
 * @property level 文章难度等级
 * @property talkItContent 文章介绍内容
 * @property talkItAudio 文章介绍音频URL
 * @property imgList 文章图片列表
 * @property readReportId 阅读报告ID
 * @property readId 阅读ID
 * @property contentList 文章内容列表
 */
data class ArticleDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("cover") val cover: String,
    @SerializedName("title") val title: String,
    @SerializedName("wordNum") val wordNum: Int,
    @SerializedName("readCount") val readCount: Int,
    @SerializedName("bgmUrl") val bgmUrl: String,
    @SerializedName("typeId") val typeId: Int,
    @SerializedName("typeName") val typeName: String,
    @SerializedName("level") val level: Int,
    @SerializedName("talkItContent") val talkItContent: String,
    @SerializedName("talkItAudio") val talkItAudio: String,
    @SerializedName("imgList") val imgList: List<String>,
    @SerializedName("readReportId") val readReportId: Int,
    @SerializedName("readId") val readId: Int,
    @SerializedName("contentList") val contentList: List<ContentItem>
)

/**
 * 文章内容项数据类
 * 表示文章中的单个页面内容
 *
 * @property pageNum 页码
 * @property imgUrl 页面图片URL
 * @property audioUrl 页面音频URL
 * @property audioDuration 音频时长（秒）
 * @property sentence 页面文本内容
 * @property frameType 框架类型
 * @property sentenceByXFList 讯飞分词结果列表
 */
data class ContentItem(
    @SerializedName("pageNum") val pageNum: Int,
    @SerializedName("imgUrl") val imgUrl: String,
    @SerializedName("audioUrl") val audioUrl: String,
    @SerializedName("audioDuration") val audioDuration: Int,
    @SerializedName("sentence") val sentence: String,
    @SerializedName("frameType") val frameType: Int,
    @SerializedName("sentenceByXFList") val sentenceByXFList: List<SentenceByXF>
)

/**
 * 讯飞分词结果数据类
 * 用于文本分词和语音合成
 *
 * @property word 分词后的单词
 * @property wb 单词开始位置
 * @property we 单词结束位置
 */
data class SentenceByXF(
    @SerializedName("word") val word: String,
    @SerializedName("wb") val wb: Int,
    @SerializedName("we") val we: Int
) 