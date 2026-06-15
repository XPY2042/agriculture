package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.agri.AgriAlarmCandidate;
import com.ruoyi.common.utils.agri.AgriThresholdChecker;
import com.ruoyi.system.domain.AgriAlarmRecord;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.system.mapper.AgriAlarmRecordMapper;
import com.ruoyi.system.mapper.AgriSensorNodeMapper;
import com.ruoyi.system.mapper.AgriSensorReadingMapper;
import com.ruoyi.system.service.IAgriAlarmService;

@Service
public class AgriAlarmServiceImpl implements IAgriAlarmService
{
    private static final int COOLDOWN_MINUTES = 30;

    @Autowired
    private AgriAlarmRecordMapper agriAlarmRecordMapper;

    @Autowired
    private AgriSensorNodeMapper agriSensorNodeMapper;

    @Autowired
    private AgriSensorReadingMapper agriSensorReadingMapper;

    @Override
    public List<AgriAlarmRecord> selectAgriAlarmRecordList(AgriAlarmRecord record)
    {
        return agriAlarmRecordMapper.selectAgriAlarmRecordList(record);
    }

    @Override
    public AgriAlarmRecord selectAgriAlarmRecordById(Long alarmId)
    {
        return agriAlarmRecordMapper.selectAgriAlarmRecordById(alarmId);
    }

    @Override
    public int countUnhandled()
    {
        return agriAlarmRecordMapper.countUnhandled();
    }

    @Override
    public int evaluateAndSave(AgriSensorReading reading, String nodeName)
    {
        if (reading == null || reading.getNodeId() == null)
        {
            return 0;
        }
        List<AgriAlarmCandidate> candidates = AgriThresholdChecker.evaluate(
            reading.getSoilMoisturePct(),
            reading.getAirTempC(),
            reading.getAirHumidityPct(),
            reading.getLightLux(),
            reading.getSoilTempC());
        if (candidates.isEmpty())
        {
            return 0;
        }
        Date alarmTime = reading.getReadingTime() != null ? reading.getReadingTime() : new Date();
        int saved = 0;
        for (AgriAlarmCandidate c : candidates)
        {
            if (agriAlarmRecordMapper.countRecentSameAlarm(
                reading.getNodeId(), c.getMetricCode(), c.getAlarmLevel(), COOLDOWN_MINUTES) > 0)
            {
                continue;
            }
            AgriAlarmRecord record = new AgriAlarmRecord();
            record.setNodeId(reading.getNodeId());
            record.setReadingId(reading.getReadingId());
            record.setMetricCode(c.getMetricCode());
            record.setMetricName(c.getMetricName());
            record.setAlarmLevel(c.getAlarmLevel());
            record.setDirection(c.getDirection());
            record.setActualValue(c.getActualValue());
            record.setThresholdValue(c.getThresholdValue());
            String msg = c.getAlarmMessage();
            if (StringUtils.isNotEmpty(nodeName))
            {
                msg = "\u3010" + nodeName + "\u3011" + msg;
            }
            record.setAlarmMessage(msg);
            record.setStatus("0");
            record.setAlarmTime(alarmTime);
            agriAlarmRecordMapper.insertAgriAlarmRecord(record);
            saved++;
        }
        return saved;
    }

    @Override
    public int confirmAlarms(Long[] alarmIds, String handleBy)
    {
        if (alarmIds == null || alarmIds.length == 0)
        {
            return 0;
        }
        int n = 0;
        Date now = new Date();
        for (Long alarmId : alarmIds)
        {
            AgriAlarmRecord existing = agriAlarmRecordMapper.selectAgriAlarmRecordById(alarmId);
            if (existing == null || "1".equals(existing.getStatus()))
            {
                continue;
            }
            AgriAlarmRecord upd = new AgriAlarmRecord();
            upd.setAlarmId(alarmId);
            upd.setStatus("1");
            upd.setHandleBy(handleBy);
            upd.setHandleTime(now);
            n += agriAlarmRecordMapper.updateAgriAlarmRecordStatus(upd);
        }
        return n;
    }

    @Override
    public int scanAllNodesLatest()
    {
        AgriSensorNode query = new AgriSensorNode();
        query.setStatus("0");
        List<AgriSensorNode> nodes = agriSensorNodeMapper.selectAgriSensorNodeList(query);
        int total = 0;
        for (AgriSensorNode node : nodes)
        {
            AgriSensorReading latest = agriSensorReadingMapper.selectLatestByNodeId(node.getNodeId());
            if (latest != null)
            {
                total += evaluateAndSave(latest, node.getNodeName());
            }
        }
        return total;
    }
}
