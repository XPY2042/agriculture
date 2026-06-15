package com.ruoyi.system.domain.vo;

import java.io.Serializable;

/**
 * 单条生长建议项
 */
public class AgriAdviceItemVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 类别：如土壤湿度、空气温度、光照等 */
    private String category;

    /** 建议说明 */
    private String suggestion;

    /**
     * 严重度：info / warning / danger
     */
    private String severity;

    public AgriAdviceItemVo()
    {
    }

    public AgriAdviceItemVo(String category, String suggestion, String severity)
    {
        this.category = category;
        this.suggestion = suggestion;
        this.severity = severity;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getSuggestion()
    {
        return suggestion;
    }

    public void setSuggestion(String suggestion)
    {
        this.suggestion = suggestion;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(String severity)
    {
        this.severity = severity;
    }
}
