package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.system.domain.vo.AgriReadingAggregateVo;

/**
 * ????????????
 */
public interface AgriSensorReadingMapper
{
    AgriSensorReading selectLatestByNodeId(Long nodeId);

    List<AgriSensorReading> selectTrendByNodeId(@Param("nodeId") Long nodeId, @Param("hours") int hours);

    List<AgriSensorReading> selectAgriSensorReadingList(AgriSensorReading reading);

    int insertAgriSensorReading(AgriSensorReading reading);

    int deleteAgriSensorReadingByIds(Long[] readingIds);

    AgriReadingAggregateVo selectAvgInWindow(@Param("nodeId") Long nodeId, @Param("hours") int hours);
}
