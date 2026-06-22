package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 报表分类/排行通用项
 */
public class ReportSeriesVo
{
    private String label;
    private String code;
    private String subLabel;
    private Long count;
    private Long doneCount;
    private BigDecimal totalCost;
    private BigDecimal ratio;
    private BigDecimal avgHours;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getSubLabel()
    {
        return subLabel;
    }

    public void setSubLabel(String subLabel)
    {
        this.subLabel = subLabel;
    }

    public Long getCount()
    {
        return count;
    }

    public void setCount(Long count)
    {
        this.count = count;
    }

    public Long getDoneCount()
    {
        return doneCount;
    }

    public void setDoneCount(Long doneCount)
    {
        this.doneCount = doneCount;
    }

    public BigDecimal getTotalCost()
    {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
    }

    public BigDecimal getRatio()
    {
        return ratio;
    }

    public void setRatio(BigDecimal ratio)
    {
        this.ratio = ratio;
    }

    public BigDecimal getAvgHours()
    {
        return avgHours;
    }

    public void setAvgHours(BigDecimal avgHours)
    {
        this.avgHours = avgHours;
    }
}
