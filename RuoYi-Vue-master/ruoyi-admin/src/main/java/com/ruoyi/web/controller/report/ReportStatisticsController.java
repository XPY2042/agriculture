package com.ruoyi.web.controller.report;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.ReportExportRowVo;
import com.ruoyi.system.domain.vo.ReportQueryVo;
import com.ruoyi.system.service.IReportStatisticsService;

/**
 * 报表统计
 */
@RestController
@RequestMapping("/report/statistics")
public class ReportStatisticsController extends BaseController
{
    @Autowired
    private IReportStatisticsService reportStatisticsService;

    @PreAuthorize("@ss.hasPermi('report:statistics:view') or @ss.hasPermi('repair:admin:list') or @ss.hasPermi('repair:tech:list') or @ss.hasPermi('agri:monitor:view')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(reportStatisticsService.getFilterOptions());
    }

    @PreAuthorize("@ss.hasPermi('report:statistics:view') or @ss.hasPermi('repair:admin:list') or @ss.hasPermi('repair:tech:list')")
    @GetMapping("/repair")
    public AjaxResult repair(ReportQueryVo query)
    {
        return success(reportStatisticsService.getRepairReport(query));
    }

    @PreAuthorize("@ss.hasPermi('report:statistics:view') or @ss.hasPermi('repair:admin:list') or @ss.hasPermi('repair:tech:list')")
    @Log(title = "维修报表", businessType = BusinessType.EXPORT)
    @PostMapping("/repair/export")
    public void exportRepair(HttpServletResponse response, ReportQueryVo query)
    {
        List<ReportExportRowVo> list = reportStatisticsService.exportRepairReport(query);
        ExcelUtil<ReportExportRowVo> util = new ExcelUtil<ReportExportRowVo>(ReportExportRowVo.class);
        util.exportExcel(response, list, "维修报表");
    }

    @PreAuthorize("@ss.hasPermi('report:statistics:view') or @ss.hasPermi('agri:monitor:view')")
    @GetMapping("/alarm")
    public AjaxResult alarm(ReportQueryVo query)
    {
        return success(reportStatisticsService.getAlarmReport(query));
    }

    @PreAuthorize("@ss.hasPermi('report:statistics:view') or @ss.hasPermi('agri:monitor:view')")
    @Log(title = "告警报表", businessType = BusinessType.EXPORT)
    @PostMapping("/alarm/export")
    public void exportAlarm(HttpServletResponse response, ReportQueryVo query)
    {
        List<ReportExportRowVo> list = reportStatisticsService.exportAlarmReport(query);
        ExcelUtil<ReportExportRowVo> util = new ExcelUtil<ReportExportRowVo>(ReportExportRowVo.class);
        util.exportExcel(response, list, "告警报表");
    }
}
