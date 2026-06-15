package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * ũҵ���и澯 agri_alarm_record
 */
public class AgriAlarmRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long alarmId;
    private Long nodeId;
    private String nodeName;
    private Long readingId;
    private String metricCode;
    private String metricName;
    /** 1���� 2���� */
    private String alarmLevel;
    private String direction;
    private String actualValue;
    private String thresholdValue;
    private String alarmMessage;
    /** 0δ���� 1��ȷ�� */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;

    private String handleBy;

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
