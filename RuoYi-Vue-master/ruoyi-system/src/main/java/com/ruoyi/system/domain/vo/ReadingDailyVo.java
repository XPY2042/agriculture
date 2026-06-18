package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 传感器日均值
 */
public class ReadingDailyVo {
    private String dateStr;
    private BigDecimal avgTemp;
    private BigDecimal avgHumidity;
    private BigDecimal avgMoisture;

    public String getDateStr() { return dateStr; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }
    public BigDecimal getAvgTemp() { return avgTemp; }
    public void setAvgTemp(BigDecimal avgTemp) { this.avgTemp = avgTemp; }
    public BigDecimal getAvgHumidity() { return avgHumidity; }
    public void setAvgHumidity(BigDecimal avgHumidity) { this.avgHumidity = avgHumidity; }
    public BigDecimal getAvgMoisture() { return avgMoisture; }
    public void setAvgMoisture(BigDecimal avgMoisture) { this.avgMoisture = avgMoisture; }
}
