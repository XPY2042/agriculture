package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 告警报表汇总
 */
public class AlarmReportSummaryVo
{
    private Long totalCount;
    private Long unconfirmedCount;
    private Long confirmedCount;
    private Long processingCount;
    private Long recoveredCount;
    private Long falsePositiveCount;
    private BigDecimal avgConfirmHours;
    private BigDecimal avgRecoverHours;

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }

    public Long getUnconfirmedCount()
    {
        return unconfirmedCount;
    }

    public void setUnconfirmedCount(Long unconfirmedCount)
    {
        this.unconfirmedCount = unconfirmedCount;
    }

    public Long getConfirmedCount()
    {
        return confirmedCount;
    }

    public void setConfirmedCount(Long confirmedCount)
    {
        this.confirmedCount = confirmedCount;
    }

    public Long getProcessingCount()
    {
        return processingCount;
    }

    public void setProcessingCount(Long processingCount)
    {
        this.processingCount = processingCount;
    }

    public Long getRecoveredCount()
    {
        return recoveredCount;
    }

    public void setRecoveredCount(Long recoveredCount)
    {
        this.recoveredCount = recoveredCount;
    }

    public Long getFalsePositiveCount()
    {
        return falsePositiveCount;
    }

    public void setFalsePositiveCount(Long falsePositiveCount)
    {
        this.falsePositiveCount = falsePositiveCount;
    }

    public BigDecimal getAvgConfirmHours()
    {
        return avgConfirmHours;
    }

    public void setAvgConfirmHours(BigDecimal avgConfirmHours)
    {
        this.avgConfirmHours = avgConfirmHours;
    }

    public BigDecimal getAvgRecoverHours()
    {
        return avgRecoverHours;
    }

    public void setAvgRecoverHours(BigDecimal avgRecoverHours)
    {
        this.avgRecoverHours = avgRecoverHours;
    }
}
