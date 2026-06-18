package com.ruoyi.web.controller.agri;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.AgriAlarmRecord;
import com.ruoyi.system.service.IAgriAlarmService;

/**
 * ????????????
 */
@RestController
@RequestMapping("/agri/alarm")
public class AgriAlarmController extends BaseController
{
    @Autowired
    private IAgriAlarmService agriAlarmService;

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/list")
    public TableDataInfo list(AgriAlarmRecord record)
    {
        startPage();
        List<AgriAlarmRecord> list = agriAlarmService.selectAgriAlarmRecordList(record);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/{alarmId}")
    public AjaxResult getInfo(@PathVariable Long alarmId)
    {
        return success(agriAlarmService.selectAgriAlarmRecordById(alarmId));
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/unhandled/count")
    public AjaxResult unhandledCount()
    {
        return success(agriAlarmService.countUnhandled());
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @Log(title = "??????", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public AjaxResult confirm(@RequestBody Long[] alarmIds)
    {
        return toAjax(agriAlarmService.confirmAlarms(alarmIds, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @Log(title = "告警扫描", businessType = BusinessType.OTHER)
    @PutMapping("/scan")
    public AjaxResult scan()
    {
        int n = agriAlarmService.scanAllNodesLatest();
        return success("扫描完成，此次生成告警 " + n + " 条");
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @Log(title = "告警数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriAlarmRecord record)
    {
        List<AgriAlarmRecord> list = agriAlarmService.selectAgriAlarmRecordList(record);
        ExcelUtil<AgriAlarmRecord> util = new ExcelUtil<AgriAlarmRecord>(AgriAlarmRecord.class);
        util.exportExcel(response, list, "告警数据");
    }
}
