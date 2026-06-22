package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.vo.ReportExportRowVo;
import com.ruoyi.system.domain.vo.ReportQueryVo;

/**
 * 报表统计服务
 */
public interface IReportStatisticsService
{
    Map<String, Object> getRepairReport(ReportQueryVo query);

    Map<String, Object> getAlarmReport(ReportQueryVo query);

    Map<String, Object> getFilterOptions();

    List<ReportExportRowVo> exportRepairReport(ReportQueryVo query);

    List<ReportExportRowVo> exportAlarmReport(ReportQueryVo query);
}
