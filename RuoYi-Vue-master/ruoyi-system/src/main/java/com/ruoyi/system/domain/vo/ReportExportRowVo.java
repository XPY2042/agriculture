package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;

/**
 * 报表导出通用行
 */
public class ReportExportRowVo
{
    @Excel(name = "报表类型")
    private String reportType;

    @Excel(name = "数据分组")
    private String sectionName;

    @Excel(name = "指标名称")
    private String itemName;

    @Excel(name = "编码")
    private String itemCode;

    @Excel(name = "说明")
    private String subLabel;

    @Excel(name = "总数")
    private Long totalCount;

    @Excel(name = "待处理/未确认")
    private Long pendingCount;

    @Excel(name = "处理中")
    private Long processingCount;

    @Excel(name = "已完成/已恢复")
    private Long doneCount;

    @Excel(name = "已取消/误报")
    private Long cancelledCount;

    @Excel(name = "超时数")
    private Long timeoutCount;

    @Excel(name = "已确认")
    private Long confirmedCount;

    @Excel(name = "比率(%)")
    private BigDecimal ratio;

    @Excel(name = "金额")
    private BigDecimal totalCost;

    @Excel(name = "平均耗时(h)")
    private BigDecimal avgHours;

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

    public String getSectionName()
    {
        return sectionName;
    }

    public void setSectionName(String sectionName)
    {
        this.sectionName = sectionName;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemCode()
    {
        return itemCode;
    }

    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    public String getSubLabel()
    {
        return subLabel;
    }

    public void setSubLabel(String subLabel)
    {
        this.subLabel = subLabel;
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

    public Long getConfirmedCount()
    {
        return confirmedCount;
    }

    public void setConfirmedCount(Long confirmedCount)
    {
        this.confirmedCount = confirmedCount;
    }

    public BigDecimal getRatio()
    {
        return ratio;
    }

    public void setRatio(BigDecimal ratio)
    {
        this.ratio = ratio;
    }

    public BigDecimal getTotalCost()
    {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
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
