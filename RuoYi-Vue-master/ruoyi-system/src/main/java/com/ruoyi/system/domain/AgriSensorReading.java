package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 农业传感器读数 agri_sensor_reading
 */
public class AgriSensorReading extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "读数ID", cellType = Excel.ColumnType.NUMERIC)
    private Long readingId;

    private Long nodeId;

    @Excel(name = "土壤湿度(%)")
    private BigDecimal soilMoisturePct;

    @Excel(name = "空气温度(℃)")
    private BigDecimal airTempC;

    @Excel(name = "空气湿度(%)")
    private BigDecimal airHumidityPct;

    @Excel(name = "光照(Lux)")
    private Integer lightLux;

    @Excel(name = "土壤温度(℃)")
    private BigDecimal soilTempC;

    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readingTime;

    public Long getReadingId()
    {
        return readingId;
    }

    public void setReadingId(Long readingId)
    {
        this.readingId = readingId;
    }

    public Long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public BigDecimal getSoilMoisturePct()
    {
        return soilMoisturePct;
    }

    public void setSoilMoisturePct(BigDecimal soilMoisturePct)
    {
        this.soilMoisturePct = soilMoisturePct;
    }

    public BigDecimal getAirTempC()
    {
        return airTempC;
    }

    public void setAirTempC(BigDecimal airTempC)
    {
        this.airTempC = airTempC;
    }

    public BigDecimal getAirHumidityPct()
    {
        return airHumidityPct;
    }

    public void setAirHumidityPct(BigDecimal airHumidityPct)
    {
        this.airHumidityPct = airHumidityPct;
    }

    public Integer getLightLux()
    {
        return lightLux;
    }

    public void setLightLux(Integer lightLux)
    {
        this.lightLux = lightLux;
    }

    public BigDecimal getSoilTempC()
    {
        return soilTempC;
    }

    public void setSoilTempC(BigDecimal soilTempC)
    {
        this.soilTempC = soilTempC;
    }

    public Date getReadingTime()
    {
        return readingTime;
    }

    public void setReadingTime(Date readingTime)
    {
        this.readingTime = readingTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("readingId", getReadingId())
            .append("nodeId", getNodeId())
            .append("soilMoisturePct", getSoilMoisturePct())
            .append("airTempC", getAirTempC())
            .append("airHumidityPct", getAirHumidityPct())
            .append("lightLux", getLightLux())
            .append("soilTempC", getSoilTempC())
            .append("readingTime", getReadingTime())
            .toString();
    }
}
