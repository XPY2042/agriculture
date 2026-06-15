package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AgriAlarmRecord;
import com.ruoyi.system.domain.AgriSensorReading;

/**
 * ũҵ���и澯����
 */
public interface IAgriAlarmService
{
    List<AgriAlarmRecord> selectAgriAlarmRecordList(AgriAlarmRecord record);

    AgriAlarmRecord selectAgriAlarmRecordById(Long alarmId);

    int countUnhandled();

    /**
     * ���ݶ��������ֵ��д��澯������ȴȥ�أ�
     *
     * @return ���������澯����
     */
    int evaluateAndSave(AgriSensorReading reading, String nodeName);

    int confirmAlarms(Long[] alarmIds, String handleBy);

    /**
     * �����������ڵ�����¶���ִ��һ�θ���
     */
    int scanAllNodesLatest();
}
