package com.ruoyi.web.controller.agri;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.AlarmStatsVo;
import com.ruoyi.system.domain.vo.AlarmTrendVo;
import com.ruoyi.system.domain.vo.NodeReadingStatsVo;
import com.ruoyi.system.domain.vo.ReadingDailyVo;
import com.ruoyi.system.mapper.AgriAlarmRecordMapper;
import com.ruoyi.system.mapper.AgriSensorReadingMapper;

/**
 * 农业模块数据统计
 */
@RestController
@RequestMapping("/agri/statistics")
public class AgriStatisticsController extends BaseController
{
    @Autowired
    private AgriAlarmRecordMapper alarmRecordMapper;

    @Autowired
    private AgriSensorReadingMapper sensorReadingMapper;

    /** 告警按指标/级别分布 */
    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/alarm/distribution")
    public AjaxResult alarmDistribution(@RequestParam(defaultValue = "metric") String type)
    {
        List<AlarmStatsVo> list = "level".equals(type)
            ? alarmRecordMapper.countByLevel()
            : alarmRecordMapper.countByMetric();
        return success(list);
    }

    /** 告警趋势 */
    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/alarm/trend")
    public AjaxResult alarmTrend(@RequestParam(defaultValue = "30") int days)
    {
        List<AlarmTrendVo> list = alarmRecordMapper.trendByDay(Math.min(days, 365));
        return success(list);
    }

    /** 各节点读数频率 */
    @PreAuthorize("@ss.hasPermi('agri:monitor:view') or @ss.hasPermi('agri:node:list')")
    @GetMapping("/node/count")
    public AjaxResult nodeReadingCount()
    {
        List<NodeReadingStatsVo> list = sensorReadingMapper.countReadingByNode();
        return success(list);
    }

    /** 指定节点日均值 */
    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/reading/daily")
    public AjaxResult readingDaily(@RequestParam Long nodeId,
                                   @RequestParam(defaultValue = "7") int days)
    {
        List<ReadingDailyVo> list = sensorReadingMapper.dailyAvg(nodeId, Math.min(days, 90));
        return success(list);
    }
}
