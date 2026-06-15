package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * ũҵ���и澯 agri_alarm_record
 */
public class AgriAlarmRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "告警ID", cellType = Excel.ColumnType.NUMERIC)
    private Long alarmId;
    private Long nodeId;
    @Excel(name = "传感器节点")
    private String nodeName;
    private Long readingId;
    @Excel(name = "指标编码")
    private String metricCode;
    @Excel(name = "指标名称")
    private String metricName;
    @Excel(name = "告警级别", readConverterExp = "1=警告,2=严重")
    private String alarmLevel;
    @Excel(name = "方向", readConverterExp = "up=高于上限,down=低于下限")
    private String direction;
    @Excel(name = "实际值")
    private String actualValue;
    @Excel(name = "阈值")
    private String thresholdValue;
    @Excel(name = "告警描述")
    private String alarmMessage;
    @Excel(name = "状态", readConverterExp = "0=未处理,1=已确认")
    private String status;

    @Excel(name = "告警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;

    @Excel(name = "处理人")
    private String handleBy;

    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    public Long getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(Long alarmId)
    {
        this.alarmId = alarmId;
    }

    public Long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public Long getReadingId()
    {
        return readingId;
    }

    public void setReadingId(Long readingId)
    {
        this.readingId = readingId;
    }

    public String getMetricCode()
    {
        return metricCode;
    }

    public void setMetricCode(String metricCode)
    {
        this.metricCode = metricCode;
    }

    public String getMetricName()
    {
        return metricName;
    }

    public void setMetricName(String metricName)
    {
        this.metricName = metricName;
    }

    public String getAlarmLevel()
    {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel)
    {
        this.alarmLevel = alarmLevel;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public String getActualValue()
    {
        return actualValue;
    }

    public void setActualValue(String actualValue)
    {
        this.actualValue = actualValue;
    }

    public String getThresholdValue()
    {
        return thresholdValue;
    }

    public void setThresholdValue(String thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    public String getAlarmMessage()
    {
        return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage)
    {
        this.alarmMessage = alarmMessage;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getAlarmTime()
    {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime)
    {
        this.alarmTime = alarmTime;
    }

    public String getHandleBy()
    {
        return handleBy;
    }

    public void setHandleBy(String handleBy)
    {
        this.handleBy = handleBy;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("alarmId", getAlarmId())
            .append("nodeId", getNodeId())
            .append("metricCode", getMetricCode())
            .append("alarmLevel", getAlarmLevel())
            .append("status", getStatus())
            .append("alarmTime", getAlarmTime())
            .toString();
    }
}
