package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AgriAlarmRecord;
import com.ruoyi.system.domain.vo.AlarmStatsVo;
import com.ruoyi.system.domain.vo.AlarmTrendVo;

/**
 * 农业传感器告警 Mapper
 */
public interface AgriAlarmRecordMapper
{
    AgriAlarmRecord selectAgriAlarmRecordById(Long alarmId);

    List<AgriAlarmRecord> selectAgriAlarmRecordList(AgriAlarmRecord record);

    int insertAgriAlarmRecord(AgriAlarmRecord record);

    int updateAgriAlarmRecordStatus(AgriAlarmRecord record);

    int countUnhandled();

    int countRecentSameAlarm(@Param("nodeId") Long nodeId, @Param("metricCode") String metricCode,
        @Param("alarmLevel") String alarmLevel, @Param("cooldownMinutes") int cooldownMinutes);

    /** 按指标统计告警数量 */
    List<AlarmStatsVo> countByMetric();

    /** 按级别统计告警数量 */
    List<AlarmStatsVo> countByLevel();

    /** 按天统计告警趋势 */
    List<AlarmTrendVo> trendByDay(@Param("days") int days);
}
