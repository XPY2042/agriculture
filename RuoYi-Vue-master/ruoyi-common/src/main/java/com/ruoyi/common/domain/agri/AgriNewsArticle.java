package com.ruoyi.common.domain.agri;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * ũҵ������Ŀ��RSS ץȡ�����
 */
public class AgriNewsArticle implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** Ψһ��ʶ������ժҪ�� */
    private String articleId;

    /** ���� */
    private String title;

    /** ժҪ */
    private String summary;

    /** ԭ������ */
    private String link;

    /** ��Դվ�� */
    private String source;

    /** ����ʱ�� */
    private String publishTime;

    public String getArticleId()
    {
        return articleId;
    }

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(String publishTime)
    {
        this.publishTime = publishTime;
    }
}
