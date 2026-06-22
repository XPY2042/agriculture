package com.ruoyi.system.domain.vo;

/**
 * 报表统计筛选条件
 */
public class ReportQueryVo
{
    private String rangeType;
    private String beginTime;
    private String endTime;
    private String farmName;
    private String plotLocation;
    private String deviceType;
    private String nodeCode;
    private String personName;
    private String alarmLevel;
    private String groupFormat;

    public String getRangeType()
    {
        return rangeType;
    }

    public void setRangeType(String rangeType)
    {
        this.rangeType = rangeType;
    }

    public String getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(String beginTime)
    {
        this.beginTime = beginTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getFarmName()
    {
        return farmName;
    }

    public void setFarmName(String farmName)
    {
        this.farmName = farmName;
    }

    public String getPlotLocation()
    {
        return plotLocation;
    }

    public void setPlotLocation(String plotLocation)
    {
        this.plotLocation = plotLocation;
    }

    public String getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public String getNodeCode()
    {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode)
    {
        this.nodeCode = nodeCode;
    }

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getAlarmLevel()
    {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel)
    {
        this.alarmLevel = alarmLevel;
    }

    public String getGroupFormat()
    {
        return groupFormat;
    }

    public void setGroupFormat(String groupFormat)
    {
        this.groupFormat = groupFormat;
    }
}
