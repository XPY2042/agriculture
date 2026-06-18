package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ʱ�䴰���ڼ��ָ��ۺϣ����ڷ�����
 */
public class AgriReadingAggregateVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private BigDecimal avgSoilMoisturePct;
    private BigDecimal avgAirTempC;
    private BigDecimal avgAirHumidityPct;
    private BigDecimal avgLightLux;
    private BigDecimal avgSoilTempC;
    private BigDecimal avgSoilPh;
    private BigDecimal avgCo2Ppm;
    private BigDecimal avgWaterPh;

    public BigDecimal getAvgSoilPh() { return avgSoilPh; }
    public void setAvgSoilPh(BigDecimal avgSoilPh) { this.avgSoilPh = avgSoilPh; }
    public BigDecimal getAvgCo2Ppm() { return avgCo2Ppm; }
    public void setAvgCo2Ppm(BigDecimal avgCo2Ppm) { this.avgCo2Ppm = avgCo2Ppm; }
    public BigDecimal getAvgWaterPh() { return avgWaterPh; }
    public void setAvgWaterPh(BigDecimal avgWaterPh) { this.avgWaterPh = avgWaterPh; }

    public BigDecimal getAvgSoilMoisturePct()
    {
        return avgSoilMoisturePct;
    }

    public void setAvgSoilMoisturePct(BigDecimal avgSoilMoisturePct)
    {
        this.avgSoilMoisturePct = avgSoilMoisturePct;
    }

    public BigDecimal getAvgAirTempC()
    {
        return avgAirTempC;
    }

    public void setAvgAirTempC(BigDecimal avgAirTempC)
    {
        this.avgAirTempC = avgAirTempC;
    }

    public BigDecimal getAvgAirHumidityPct()
    {
        return avgAirHumidityPct;
    }

    public void setAvgAirHumidityPct(BigDecimal avgAirHumidityPct)
    {
        this.avgAirHumidityPct = avgAirHumidityPct;
    }

    public BigDecimal getAvgLightLux()
    {
        return avgLightLux;
    }

    public void setAvgLightLux(BigDecimal avgLightLux)
    {
        this.avgLightLux = avgLightLux;
    }

    public BigDecimal getAvgSoilTempC()
    {
        return avgSoilTempC;
    }

    public void setAvgSoilTempC(BigDecimal avgSoilTempC)
    {
        this.avgSoilTempC = avgSoilTempC;
    }
}
