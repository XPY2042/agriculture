package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.system.domain.vo.AgriReadingAggregateVo;
import com.ruoyi.system.domain.vo.NodeReadingStatsVo;
import com.ruoyi.system.domain.vo.ReadingDailyVo;

/**
 * 农业传感器读数 Mapper
 */
public interface AgriSensorReadingMapper
{
    AgriSensorReading selectLatestByNodeId(Long nodeId);

    List<AgriSensorReading> selectTrendByNodeId(@Param("nodeId") Long nodeId, @Param("hours") int hours);

    List<AgriSensorReading> selectAgriSensorReadingList(AgriSensorReading reading);

    int insertAgriSensorReading(AgriSensorReading reading);

    int deleteAgriSensorReadingByIds(Long[] readingIds);

    AgriReadingAggregateVo selectAvgInWindow(@Param("nodeId") Long nodeId, @Param("hours") int hours);

    /** 各节点读数数量统计 */
    List<NodeReadingStatsVo> countReadingByNode();

    /** 指定节点日均值 */
    List<ReadingDailyVo> dailyAvg(@Param("nodeId") Long nodeId, @Param("days") int days);
}
