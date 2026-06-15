package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于窗口读数的作物生长建议综合分析结果
 */
public class AgriGrowthAdviceVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private Long nodeId;

    /** 节点名称 */
    private String nodeName;

    /** 作物类型 */
    private String cropType;

    /** 综合等级：good / warning / risk */
    private String overallLevel;

    /** 最新读数摘要 */
    private BigDecimal latestSoilMoisturePct;
    private BigDecimal latestAirTempC;
    private BigDecimal latestAirHumidityPct;
    private Integer latestLightLux;
    private BigDecimal latestSoilTempC;

    /** 窗口均值（与趋势接口一致的小时窗） */
    private Integer windowHours;

    private BigDecimal avgSoilMoisturePct;
    private BigDecimal avgAirTempC;
    private BigDecimal avgAirHumidityPct;
    private BigDecimal avgLightLux;

    private List<AgriAdviceItemVo> items = new ArrayList<>();

    /** 0-100 综合健康评分（规则加权，非机器学习模型） */
    private Integer healthScore;

    /** 时间窗内土壤湿度等趋势一句话 */
    private String trendHint;

    /** 智能生成的小结（置顶展示） */
    private String smartSummary;

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

    public String getCropType()
    {
        return cropType;
    }

    public void setCropType(String cropType)
    {
        this.cropType = cropType;
    }

    public String getOverallLevel()
    {
        return overallLevel;
    }

    public void setOverallLevel(String overallLevel)
    {
        this.overallLevel = overallLevel;
    }

    public BigDecimal getLatestSoilMoisturePct()
    {
        return latestSoilMoisturePct;
    }

    public void setLatestSoilMoisturePct(BigDecimal latestSoilMoisturePct)
    {
        this.latestSoilMoisturePct = latestSoilMoisturePct;
    }

    public BigDecimal getLatestAirTempC()
    {
        return latestAirTempC;
    }

    public void setLatestAirTempC(BigDecimal latestAirTempC)
    {
        this.latestAirTempC = latestAirTempC;
    }

    public BigDecimal getLatestAirHumidityPct()
    {
        return latestAirHumidityPct;
    }

    public void setLatestAirHumidityPct(BigDecimal latestAirHumidityPct)
    {
        this.latestAirHumidityPct = latestAirHumidityPct;
    }

    public Integer getLatestLightLux()
    {
        return latestLightLux;
    }

    public void setLatestLightLux(Integer latestLightLux)
    {
        this.latestLightLux = latestLightLux;
    }

    public BigDecimal getLatestSoilTempC()
    {
        return latestSoilTempC;
    }

    public void setLatestSoilTempC(BigDecimal latestSoilTempC)
    {
        this.latestSoilTempC = latestSoilTempC;
    }

    public Integer getWindowHours()
    {
        return windowHours;
    }

    public void setWindowHours(Integer windowHours)
    {
        this.windowHours = windowHours;
    }

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

    public List<AgriAdviceItemVo> getItems()
    {
        return items;
    }

    public void setItems(List<AgriAdviceItemVo> items)
    {
        this.items = items;
    }

    public Integer getHealthScore()
    {
        return healthScore;
    }

    public void setHealthScore(Integer healthScore)
    {
        this.healthScore = healthScore;
    }

    public String getTrendHint()
    {
        return trendHint;
    }

    public void setTrendHint(String trendHint)
    {
        this.trendHint = trendHint;
    }

    public String getSmartSummary()
    {
        return smartSummary;
    }

    public void setSmartSummary(String smartSummary)
    {
        this.smartSummary = smartSummary;
    }
}
