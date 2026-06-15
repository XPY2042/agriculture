package com.ruoyi.web.controller.repair;

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
import com.ruoyi.system.domain.RepairRequest;
import com.ruoyi.system.service.IRepairRequestService;

/**
 * 维修人员工单管理
 */
@RestController
@RequestMapping("/repair/technician")
public class RepairTechnicianController extends BaseController
{
    @Autowired
    private IRepairRequestService repairRequestService;

    /**
     * 工单池列表 - 所有待受理报修
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:pool')")
    @GetMapping("/pool")
    public TableDataInfo pool(RepairRequest repairRequest)
    {
        startPage();
        List<RepairRequest> list = repairRequestService.selectTechnicianPool(repairRequest);
        return getDataTable(list);
    }

    /**
     * 受理工单 - 维修人员认领报修
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:accept')")
    @Log(title = "受理工单", businessType = BusinessType.UPDATE)
    @PutMapping("/accept/{requestId}")
    public AjaxResult accept(@PathVariable Long requestId)
    {
        RepairRequest existing = repairRequestService.selectRepairRequestById(requestId);
        if (existing == null || !RepairRequest.STATUS_PENDING.equals(existing.getStatus()))
        {
            return error("该工单不可受理（已被其他人受理或已取消）");
        }
        RepairRequest update = new RepairRequest();
        update.setRequestId(requestId);
        update.setTechnicianId(getUserId());
        update.setUpdateBy(getUsername());
        return toAjax(repairRequestService.acceptRepairRequest(update));
    }

    /**
     * 我的工单 - 已受理的工单列表
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:list')")
    @GetMapping("/my-tickets")
    public TableDataInfo myTickets(RepairRequest repairRequest)
    {
        repairRequest.setTechnicianId(getUserId());
        startPage();
        List<RepairRequest> list = repairRequestService.selectTechnicianAssigned(repairRequest);
        return getDataTable(list);
    }

    /**
     * 开始维修
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:start')")
    @Log(title = "开始维修", businessType = BusinessType.UPDATE)
    @PutMapping("/start/{requestId}")
    public AjaxResult startRepair(@PathVariable Long requestId)
    {
        RepairRequest existing = repairRequestService.selectRepairRequestById(requestId);
        if (existing == null)
        {
            return error("工单不存在");
        }
        if (!RepairRequest.STATUS_PROCESSING.equals(existing.getStatus()))
        {
            return error("当前工单状态不可开始维修");
        }
        if (!getUserId().equals(existing.getTechnicianId()))
        {
            return error("只能操作自己受理的工单");
        }
        RepairRequest update = new RepairRequest();
        update.setRequestId(requestId);
        update.setTechnicianId(getUserId());
        update.setUpdateBy(getUsername());
        return toAjax(repairRequestService.startRepairRequest(update));
    }

    /**
     * 完成维修 - 含维修日志回填
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:complete')")
    @Log(title = "完成维修", businessType = BusinessType.UPDATE)
    @PutMapping("/complete")
    public AjaxResult complete(@RequestBody RepairRequest repairRequest)
    {
        RepairRequest existing = repairRequestService.selectRepairRequestById(repairRequest.getRequestId());
        if (existing == null)
        {
            return error("工单不存在");
        }
        if (!RepairRequest.STATUS_PROCESSING.equals(existing.getStatus()))
        {
            return error("当前工单状态不可完成，请确认工单处于处理中");
        }
        if (!getUserId().equals(existing.getTechnicianId()))
        {
            return error("只能操作自己受理的工单");
        }
        repairRequest.setTechnicianId(getUserId());
        repairRequest.setUpdateBy(getUsername());
        return toAjax(repairRequestService.completeRepairRequest(repairRequest));
    }

    /**
     * 维修记录 - 已完成工单历史
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:history')")
    @GetMapping("/history")
    public TableDataInfo history(RepairRequest repairRequest)
    {
        repairRequest.setTechnicianId(getUserId());
        startPage();
        List<RepairRequest> list = repairRequestService.selectTechnicianHistory(repairRequest);
        return getDataTable(list);
    }

    /**
     * 导出维修记录
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:history')")
    @Log(title = "维修记录", businessType = BusinessType.EXPORT)
    @PostMapping("/history/export")
    public void exportHistory(HttpServletResponse response, RepairRequest repairRequest)
    {
        repairRequest.setTechnicianId(getUserId());
        List<RepairRequest> list = repairRequestService.selectTechnicianHistory(repairRequest);
        ExcelUtil<RepairRequest> util = new ExcelUtil<RepairRequest>(RepairRequest.class);
        util.exportExcel(response, list, "维修记录");
    }

    /**
     * 工单详情
     */
    @PreAuthorize("@ss.hasPermi('repair:tech:query')")
    @GetMapping(value = "/{requestId}")
    public AjaxResult getInfo(@PathVariable Long requestId)
    {
        RepairRequest repairRequest = repairRequestService.selectRepairRequestById(requestId);
        return success(repairRequest);
    }

    @PreAuthorize("@ss.hasPermi('repair:tech:pool')")
    @Log(title = "工单池", businessType = BusinessType.EXPORT)
    @PostMapping("/pool/export")
    public void exportPool(HttpServletResponse response, RepairRequest repairRequest)
    {
        List<RepairRequest> list = repairRequestService.selectTechnicianPool(repairRequest);
        ExcelUtil<RepairRequest> util = new ExcelUtil<RepairRequest>(RepairRequest.class);
        util.exportExcel(response, list, "工单池");
    }

    @PreAuthorize("@ss.hasPermi('repair:tech:list')")
    @Log(title = "我的工单", businessType = BusinessType.EXPORT)
    @PostMapping("/my-tickets/export")
    public void exportMyTickets(HttpServletResponse response, RepairRequest repairRequest)
    {
        repairRequest.setTechnicianId(getUserId());
        List<RepairRequest> list = repairRequestService.selectTechnicianAssigned(repairRequest);
        ExcelUtil<RepairRequest> util = new ExcelUtil<RepairRequest>(RepairRequest.class);
        util.exportExcel(response, list, "我的工单");
    }
}
