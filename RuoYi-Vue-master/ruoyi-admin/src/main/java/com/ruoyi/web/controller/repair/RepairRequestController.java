package com.ruoyi.web.controller.repair;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.system.domain.RepairRequest;
import com.ruoyi.system.service.IRepairRequestService;

/**
 * 报修申请
 */
@RestController
@RequestMapping("/repair/request")
public class RepairRequestController extends BaseController
{
    @Autowired
    private IRepairRequestService repairRequestService;

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("@ss.hasPermi('repair:apply:list') or @ss.hasPermi('repair:admin:list')")
    @GetMapping("/list")
    public TableDataInfo list(RepairRequest repairRequest)
    {
        if (!isRepairAdmin())
        {
            repairRequest.setUserId(getUserId());
        }
        startPage();
        List<RepairRequest> list = repairRequestService.selectRepairRequestList(repairRequest);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:query') or @ss.hasPermi('repair:admin:query')")
    @GetMapping(value = "/{requestId}")
    public AjaxResult getInfo(@PathVariable Long requestId)
    {
        RepairRequest repairRequest = repairRequestService.selectRepairRequestById(requestId);
        repairRequestService.checkRepairRequestOwner(repairRequest, getUserId(), isAdminView());
        return success(repairRequest);
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:add')")
    @Log(title = "报修申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody RepairRequest repairRequest)
    {
        rejectAdminUserRepair();
        repairRequest.setUserId(getUserId());
        repairRequest.setCreateBy(getUsername());
        return toAjax(repairRequestService.insertRepairRequest(repairRequest));
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:cancel')")
    @Log(title = "取消报修", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel")
    public AjaxResult cancel(@RequestBody RepairRequest repairRequest)
    {
        rejectAdminUserRepair();
        RepairRequest existing = repairRequestService.selectRepairRequestById(repairRequest.getRequestId());
        repairRequestService.checkRepairRequestOwner(existing, getUserId(), false);
        repairRequest.setUpdateBy(getUsername());
        return toAjax(repairRequestService.cancelRepairRequest(repairRequest));
    }

    @PreAuthorize("@ss.hasPermi('repair:admin:edit')")
    @Log(title = "处理报修", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody RepairRequest repairRequest)
    {
        repairRequest.setUpdateBy(getUsername());
        return toAjax(repairRequestService.handleRepairRequest(repairRequest));
    }

    @PreAuthorize("@ss.hasPermi('repair:admin:remove')")
    @Log(title = "报修申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{requestIds}")
    public AjaxResult remove(@PathVariable Long[] requestIds)
    {
        return toAjax(repairRequestService.deleteRepairRequestByIds(requestIds));
    }

    private boolean isAdminView()
    {
        return isRepairAdmin();
    }

    /** 是否以管理员身份查看/处理全部用户报修 */
    private boolean isRepairAdmin()
    {
        return SecurityUtils.isAdmin()
            || permissionService.hasPermi("repair:admin:list")
            || permissionService.hasPermi("repair:admin:query")
            || permissionService.hasPermi("repair:admin:edit");
    }

    /** 管理员仅处理报修，不可使用用户端提交/取消 */
    private void rejectAdminUserRepair()
    {
        if (SecurityUtils.isAdmin())
        {
            throw new ServiceException("管理员请使用「报修管理」处理用户报修");
        }
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:list') or @ss.hasPermi('repair:admin:list')")
    @Log(title = "报修数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepairRequest repairRequest)
    {
        if (!isRepairAdmin())
        {
            repairRequest.setUserId(getUserId());
        }
        List<RepairRequest> list = repairRequestService.selectRepairRequestList(repairRequest);
        ExcelUtil<RepairRequest> util = new ExcelUtil<RepairRequest>(RepairRequest.class);
        util.exportExcel(response, list, "报修数据");
    }
}
