package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AgriAlarmRecord;

/**
 * ũҵ���и澯 Mapper
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
}
