package com.ruoyi.system.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.system.domain.vo.AgriGrowthAdviceVo;

/** Agri sensor reading service. */
public interface IAgriSensorReadingService
{
    AgriSensorReading selectLatestByNodeId(Long nodeId);

    List<AgriSensorReading> selectTrendByNodeId(Long nodeId, int hours);

    List<AgriSensorReading> selectAgriSensorReadingList(AgriSensorReading reading);

    int insertAgriSensorReading(AgriSensorReading reading);

    int deleteAgriSensorReadingByIds(Long[] readingIds);

    /**
     * 结合阈值、作物类型、时间窗趋势与可选公网气象，生成生长建议。
     *
     * @param outdoorAirTempC 公网参考气温（℃），可为 null
     * @param outdoorRhPct 公网参考相对湿度（%），可为 null
     * @param outdoorObservationTime 公网观测时间字符串，可为 null
     */
    AgriGrowthAdviceVo analyzeGrowthAdvice(Long nodeId, int windowHours, BigDecimal outdoorAirTempC,
        Integer outdoorRhPct, String outdoorObservationTime);

    default AgriGrowthAdviceVo analyzeGrowthAdvice(Long nodeId, int windowHours)
    {
        return analyzeGrowthAdvice(nodeId, windowHours, null, null, null);
    }
}
