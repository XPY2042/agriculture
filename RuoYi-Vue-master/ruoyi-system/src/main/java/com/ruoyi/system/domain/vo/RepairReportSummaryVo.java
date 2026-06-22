package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 维修报表汇总
 */
public class RepairReportSummaryVo
{
    private Long totalCount;
    private Long pendingCount;
    private Long processingCount;
    private Long doneCount;
    private Long cancelledCount;
    private Long timeoutCount;
    private BigDecimal avgResponseHours;
    private BigDecimal avgCompleteHours;
    private BigDecimal timeoutRate;

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }

    public Long getPendingCount()
    {
        return pendingCount;
    }

    public void setPendingCount(Long pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public Long getProcessingCount()
    {
        return processingCount;
    }

    public void setProcessingCount(Long processingCount)
    {
        this.processingCount = processingCount;
    }

    public Long getDoneCount()
    {
        return doneCount;
    }

    public void setDoneCount(Long doneCount)
    {
        this.doneCount = doneCount;
    }

    public Long getCancelledCount()
    {
        return cancelledCount;
    }

    public void setCancelledCount(Long cancelledCount)
    {
        this.cancelledCount = cancelledCount;
    }

    public Long getTimeoutCount()
    {
        return timeoutCount;
    }

    public void setTimeoutCount(Long timeoutCount)
    {
        this.timeoutCount = timeoutCount;
    }

    public BigDecimal getAvgResponseHours()
    {
        return avgResponseHours;
    }

    public void setAvgResponseHours(BigDecimal avgResponseHours)
    {
        this.avgResponseHours = avgResponseHours;
    }

    public BigDecimal getAvgCompleteHours()
    {
        return avgCompleteHours;
    }

    public void setAvgCompleteHours(BigDecimal avgCompleteHours)
    {
        this.avgCompleteHours = avgCompleteHours;
    }

    public BigDecimal getTimeoutRate()
    {
        return timeoutRate;
    }

    public void setTimeoutRate(BigDecimal timeoutRate)
    {
        this.timeoutRate = timeoutRate;
    }
}
