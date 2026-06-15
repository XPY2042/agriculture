package com.ruoyi.common.utils.agri;

/**
 * ������ֵԽ���ѡ�����ǰ��
 */
public class AgriAlarmCandidate
{
    private String metricCode;
    private String metricName;
    /** 1���� 2���� */
    private String alarmLevel;
    private String direction;
    private String actualValue;
    private String thresholdValue;
    private String alarmMessage;

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
}
