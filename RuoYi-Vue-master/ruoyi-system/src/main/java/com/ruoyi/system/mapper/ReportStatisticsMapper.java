package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.vo.AlarmReportSummaryVo;
import com.ruoyi.system.domain.vo.RepairReportSummaryVo;
import com.ruoyi.system.domain.vo.ReportOptionVo;
import com.ruoyi.system.domain.vo.ReportQueryVo;
import com.ruoyi.system.domain.vo.ReportSeriesVo;
import com.ruoyi.system.domain.vo.ReportTrendVo;

/**
 * 报表统计 Mapper
 */
public interface ReportStatisticsMapper
{
    RepairReportSummaryVo selectRepairSummary(ReportQueryVo query);

    List<ReportTrendVo> selectRepairTrend(ReportQueryVo query);

    List<ReportSeriesVo> selectRepairByDeviceType(ReportQueryVo query);

    List<ReportSeriesVo> selectRepairByFaultType(ReportQueryVo query);

    List<ReportSeriesVo> selectRepairByTechnician(ReportQueryVo query);

    List<ReportSeriesVo> selectRepairDeviceTop(ReportQueryVo query);

    AlarmReportSummaryVo selectAlarmSummary(ReportQueryVo query);

    List<ReportTrendVo> selectAlarmTrend(ReportQueryVo query);

    List<ReportSeriesVo> selectAlarmLevelShare(ReportQueryVo query);

    List<ReportSeriesVo> selectAlarmByDeviceType(ReportQueryVo query);

    List<ReportSeriesVo> selectAlarmByArea(ReportQueryVo query);

    List<ReportSeriesVo> selectAlarmDeviceTop(ReportQueryVo query);

    List<ReportSeriesVo> selectAlarmRepeatedDeviceTop(ReportQueryVo query);

    List<ReportOptionVo> selectFarmOptions();

    List<ReportOptionVo> selectPlotOptions();

    List<ReportOptionVo> selectDeviceTypeOptions();

    List<ReportOptionVo> selectDeviceCodeOptions();

    List<ReportOptionVo> selectPersonOptions();
}
