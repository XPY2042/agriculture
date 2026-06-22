package com.ruoyi.system.domain.vo;

/**
 * 报表趋势点
 */
public class ReportTrendVo
{
    private String label;
    private Long totalCount;
    private Long pendingCount;
    private Long processingCount;
    private Long doneCount;
    private Long cancelledCount;
    private Long timeoutCount;
    private Long unconfirmedCount;
    private Long confirmedCount;
    private Long recoveredCount;
    private Long falsePositiveCount;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

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
}
