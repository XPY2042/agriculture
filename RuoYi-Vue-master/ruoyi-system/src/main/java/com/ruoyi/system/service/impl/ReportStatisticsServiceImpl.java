package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.vo.AlarmReportSummaryVo;
import com.ruoyi.system.domain.vo.RepairReportSummaryVo;
import com.ruoyi.system.domain.vo.ReportExportRowVo;
import com.ruoyi.system.domain.vo.ReportQueryVo;
import com.ruoyi.system.domain.vo.ReportSeriesVo;
import com.ruoyi.system.domain.vo.ReportTrendVo;
import com.ruoyi.system.mapper.ReportStatisticsMapper;
import com.ruoyi.system.service.IReportStatisticsService;

/**
 * 报表统计服务实现
 */
@Service
public class ReportStatisticsServiceImpl implements IReportStatisticsService
{
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ReportStatisticsMapper reportStatisticsMapper;

    @Override
    public Map<String, Object> getRepairReport(ReportQueryVo query)
    {
        normalizeQuery(query);
        RepairReportSummaryVo summary = reportStatisticsMapper.selectRepairSummary(query);
        if (summary == null)
        {
            summary = new RepairReportSummaryVo();
        }
        summary.setTimeoutRate(percent(summary.getTimeoutCount(), summary.getTotalCount()));

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("summary", summary);
        result.put("trend", reportStatisticsMapper.selectRepairTrend(query));
        result.put("deviceTypeStats", reportStatisticsMapper.selectRepairByDeviceType(query));
        result.put("faultTypeStats", reportStatisticsMapper.selectRepairByFaultType(query));
        result.put("technicianStats", reportStatisticsMapper.selectRepairByTechnician(query));
        result.put("deviceTop10", reportStatisticsMapper.selectRepairDeviceTop(query));
        return result;
    }

    @Override
    public Map<String, Object> getAlarmReport(ReportQueryVo query)
    {
        normalizeQuery(query);
        AlarmReportSummaryVo summary = reportStatisticsMapper.selectAlarmSummary(query);
        if (summary == null)
        {
            summary = new AlarmReportSummaryVo();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("summary", summary);
        result.put("trend", reportStatisticsMapper.selectAlarmTrend(query));
        result.put("levelShare", reportStatisticsMapper.selectAlarmLevelShare(query));
        result.put("deviceTypeStats", reportStatisticsMapper.selectAlarmByDeviceType(query));
        result.put("areaStats", reportStatisticsMapper.selectAlarmByArea(query));
        result.put("deviceTop10", reportStatisticsMapper.selectAlarmDeviceTop(query));
        result.put("repeatTop10", reportStatisticsMapper.selectAlarmRepeatedDeviceTop(query));
        return result;
    }

    @Override
    public Map<String, Object> getFilterOptions()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("farmOptions", reportStatisticsMapper.selectFarmOptions());
        result.put("plotOptions", reportStatisticsMapper.selectPlotOptions());
        result.put("deviceTypeOptions", reportStatisticsMapper.selectDeviceTypeOptions());
        result.put("deviceCodeOptions", reportStatisticsMapper.selectDeviceCodeOptions());
        result.put("personOptions", reportStatisticsMapper.selectPersonOptions());
        return result;
    }

    @Override
    public List<ReportExportRowVo> exportRepairReport(ReportQueryVo query)
    {
        query = ensureQuery(query);
        normalizeQuery(query);
        RepairReportSummaryVo summary = reportStatisticsMapper.selectRepairSummary(query);
        if (summary == null)
        {
            summary = new RepairReportSummaryVo();
        }
        summary.setTimeoutRate(percent(summary.getTimeoutCount(), summary.getTotalCount()));

        List<ReportExportRowVo> rows = new ArrayList<ReportExportRowVo>();
        addRepairSummaryRows(rows, summary);
        addRepairTrendRows(rows, reportStatisticsMapper.selectRepairTrend(query));
        addSeriesRows(rows, "维修报表", "设备类型维修分布", reportStatisticsMapper.selectRepairByDeviceType(query));
        addSeriesRows(rows, "维修报表", "故障类型分布", reportStatisticsMapper.selectRepairByFaultType(query));
        addSeriesRows(rows, "维修报表", "维修人员效率", reportStatisticsMapper.selectRepairByTechnician(query));
        addSeriesRows(rows, "维修报表", "故障最多设备Top10", reportStatisticsMapper.selectRepairDeviceTop(query));
        return rows;
    }

    @Override
    public List<ReportExportRowVo> exportAlarmReport(ReportQueryVo query)
    {
        query = ensureQuery(query);
        normalizeQuery(query);
        AlarmReportSummaryVo summary = reportStatisticsMapper.selectAlarmSummary(query);
        if (summary == null)
        {
            summary = new AlarmReportSummaryVo();
        }

        List<ReportExportRowVo> rows = new ArrayList<ReportExportRowVo>();
        addAlarmSummaryRows(rows, summary);
        addAlarmTrendRows(rows, reportStatisticsMapper.selectAlarmTrend(query));
        addSeriesRows(rows, "告警报表", "告警等级占比", reportStatisticsMapper.selectAlarmLevelShare(query));
        addSeriesRows(rows, "告警报表", "设备类型告警分布", reportStatisticsMapper.selectAlarmByDeviceType(query));
        addSeriesRows(rows, "告警报表", "区域告警分布", reportStatisticsMapper.selectAlarmByArea(query));
        addSeriesRows(rows, "告警报表", "告警最多设备Top10", reportStatisticsMapper.selectAlarmDeviceTop(query));
        addSeriesRows(rows, "告警报表", "重复告警设备Top10", reportStatisticsMapper.selectAlarmRepeatedDeviceTop(query));
        return rows;
    }

    private ReportQueryVo ensureQuery(ReportQueryVo query)
    {
        return query == null ? new ReportQueryVo() : query;
    }

    private void addRepairSummaryRows(List<ReportExportRowVo> rows, RepairReportSummaryVo summary)
    {
        rows.add(countRow("维修报表", "汇总指标", "工单总数", summary.getTotalCount()));
        rows.add(countRow("维修报表", "汇总指标", "待处理", summary.getPendingCount()));
        rows.add(countRow("维修报表", "汇总指标", "处理中", summary.getProcessingCount()));
        rows.add(countRow("维修报表", "汇总指标", "已完成", summary.getDoneCount()));
        rows.add(countRow("维修报表", "汇总指标", "已取消", summary.getCancelledCount()));
        rows.add(countRow("维修报表", "汇总指标", "超时工单", summary.getTimeoutCount()));
        rows.add(decimalRow("维修报表", "汇总指标", "超时率", summary.getTimeoutRate(), "ratio"));
        rows.add(decimalRow("维修报表", "汇总指标", "平均响应耗时", summary.getAvgResponseHours(), "avgHours"));
        rows.add(decimalRow("维修报表", "汇总指标", "平均完成耗时", summary.getAvgCompleteHours(), "avgHours"));
    }

    private void addAlarmSummaryRows(List<ReportExportRowVo> rows, AlarmReportSummaryVo summary)
    {
        rows.add(countRow("告警报表", "汇总指标", "告警总数", summary.getTotalCount()));
        rows.add(countRow("告警报表", "汇总指标", "未确认", summary.getUnconfirmedCount()));
        rows.add(countRow("告警报表", "汇总指标", "已确认", summary.getConfirmedCount()));
        rows.add(countRow("告警报表", "汇总指标", "处理中", summary.getProcessingCount()));
        rows.add(countRow("告警报表", "汇总指标", "已恢复", summary.getRecoveredCount()));
        rows.add(countRow("告警报表", "汇总指标", "误报数", summary.getFalsePositiveCount()));
        rows.add(decimalRow("告警报表", "汇总指标", "平均确认耗时", summary.getAvgConfirmHours(), "avgHours"));
        rows.add(decimalRow("告警报表", "汇总指标", "平均恢复耗时", summary.getAvgRecoverHours(), "avgHours"));
    }

    private void addRepairTrendRows(List<ReportExportRowVo> rows, List<ReportTrendVo> trend)
    {
        if (trend == null)
        {
            return;
        }
        for (ReportTrendVo item : trend)
        {
            ReportExportRowVo row = baseRow("维修报表", "工单趋势", item.getLabel());
            row.setTotalCount(item.getTotalCount());
            row.setPendingCount(item.getPendingCount());
            row.setProcessingCount(item.getProcessingCount());
            row.setDoneCount(item.getDoneCount());
            row.setCancelledCount(item.getCancelledCount());
            row.setTimeoutCount(item.getTimeoutCount());
            rows.add(row);
        }
    }

    private void addAlarmTrendRows(List<ReportExportRowVo> rows, List<ReportTrendVo> trend)
    {
        if (trend == null)
        {
            return;
        }
        for (ReportTrendVo item : trend)
        {
            ReportExportRowVo row = baseRow("告警报表", "告警趋势", item.getLabel());
            row.setTotalCount(item.getTotalCount());
            row.setPendingCount(item.getUnconfirmedCount());
            row.setConfirmedCount(item.getConfirmedCount());
            row.setProcessingCount(item.getProcessingCount());
            row.setDoneCount(item.getRecoveredCount());
            row.setCancelledCount(item.getFalsePositiveCount());
            rows.add(row);
        }
    }

    private void addSeriesRows(List<ReportExportRowVo> rows, String reportType, String sectionName, List<ReportSeriesVo> list)
    {
        if (list == null)
        {
            return;
        }
        for (ReportSeriesVo item : list)
        {
            ReportExportRowVo row = baseRow(reportType, sectionName, item.getLabel());
            row.setItemCode(item.getCode());
            row.setSubLabel(item.getSubLabel());
            row.setTotalCount(item.getCount());
            row.setDoneCount(item.getDoneCount());
            row.setRatio(item.getRatio());
            row.setTotalCost(item.getTotalCost());
            row.setAvgHours(item.getAvgHours());
            rows.add(row);
        }
    }

    private ReportExportRowVo countRow(String reportType, String sectionName, String itemName, Long count)
    {
        ReportExportRowVo row = baseRow(reportType, sectionName, itemName);
        row.setTotalCount(count);
        return row;
    }

    private ReportExportRowVo decimalRow(String reportType, String sectionName, String itemName, BigDecimal value, String target)
    {
        ReportExportRowVo row = baseRow(reportType, sectionName, itemName);
        if ("ratio".equals(target))
        {
            row.setRatio(value);
        }
        else
        {
            row.setAvgHours(value);
        }
        return row;
    }

    private ReportExportRowVo baseRow(String reportType, String sectionName, String itemName)
    {
        ReportExportRowVo row = new ReportExportRowVo();
        row.setReportType(reportType);
        row.setSectionName(sectionName);
        row.setItemName(itemName);
        return row;
    }

    private void normalizeQuery(ReportQueryVo query)
    {
        if (query == null)
        {
            return;
        }
        String rangeType = StringUtils.isEmpty(query.getRangeType()) ? "month" : query.getRangeType();
        LocalDateTime[] range = resolveRange(rangeType, query.getBeginTime(), query.getEndTime());
        query.setBeginTime(formatDateTime(range[0]));
        query.setEndTime(formatDateTime(range[1]));
        query.setGroupFormat(resolveGroupFormat(range[0], range[1]));
    }

    private LocalDateTime[] resolveRange(String rangeType, String beginTime, String endTime)
    {
        LocalDateTime now = LocalDateTime.now();
        if ("custom".equals(rangeType) && StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime))
        {
            return new LocalDateTime[] { parseDateTime(beginTime), parseDateTime(endTime) };
        }

        if ("today".equals(rangeType))
        {
            LocalDate today = now.toLocalDate();
            return new LocalDateTime[] { today.atStartOfDay(), today.atTime(LocalTime.MAX) };
        }
        if ("week".equals(rangeType))
        {
            LocalDate today = now.toLocalDate();
            LocalDate start = today.minusDays(today.getDayOfWeek().getValue() - 1L);
            return new LocalDateTime[] { start.atStartOfDay(), start.plusDays(6).atTime(LocalTime.MAX) };
        }
        if ("year".equals(rangeType))
        {
            LocalDate first = LocalDate.of(now.getYear(), 1, 1);
            LocalDate last = LocalDate.of(now.getYear(), 12, 31);
            return new LocalDateTime[] { first.atStartOfDay(), last.atTime(LocalTime.MAX) };
        }

        LocalDate firstDay = now.toLocalDate().withDayOfMonth(1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
        return new LocalDateTime[] { firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX) };
    }

    private String resolveGroupFormat(LocalDateTime begin, LocalDateTime end)
    {
        long days = java.time.Duration.between(begin, end).toDays();
        if (days <= 1)
        {
            return "%m-%d %H:00";
        }
        if (days <= 31)
        {
            return "%m-%d";
        }
        return "%Y-%m";
    }

    private LocalDateTime parseDateTime(String value)
    {
        String normalized = value.trim();
        if (normalized.length() == 10)
        {
            return LocalDate.parse(normalized).atStartOfDay();
        }
        return LocalDateTime.parse(normalized.replace("T", " "), DATETIME_FORMATTER);
    }

    private String formatDateTime(LocalDateTime value)
    {
        return value.format(DATETIME_FORMATTER);
    }

    private BigDecimal percent(Long part, Long total)
    {
        long totalValue = total == null ? 0L : total.longValue();
        long partValue = part == null ? 0L : part.longValue();
        if (totalValue <= 0L)
        {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(partValue)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(totalValue), 2, RoundingMode.HALF_UP);
    }
}
