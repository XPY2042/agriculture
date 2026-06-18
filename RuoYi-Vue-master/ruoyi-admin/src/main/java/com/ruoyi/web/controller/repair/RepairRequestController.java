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

    @PreAuthorize("@ss.hasPermi('repair:apply:list') or @ss.hasPermi('repair:admin:list') or @ss.hasPermi('repair:worker:list')")
    @GetMapping("/list")
    public TableDataInfo list(RepairRequest repairRequest)
    {
        if (!isRepairHandler())
        {
            repairRequest.setUserId(getUserId());
        }
        startPage();
        List<RepairRequest> list = repairRequestService.selectRepairRequestList(repairRequest);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:query') or @ss.hasPermi('repair:admin:query') or @ss.hasPermi('repair:worker:query')")
    @GetMapping(value = "/{requestId}")
    public AjaxResult getInfo(@PathVariable Long requestId)
    {
        RepairRequest repairRequest = repairRequestService.selectRepairRequestById(requestId);
        repairRequestService.checkRepairRequestOwner(repairRequest, getUserId(), isHandlerView());
        return success(repairRequest);
    }

    @PreAuthorize("@ss.hasPermi('repair:worker:count') or @ss.hasPermi('repair:admin:list')")
    @GetMapping("/pending-count")
    public AjaxResult pendingCount()
    {
        RepairRequest query = new RepairRequest();
        query.setStatus(RepairRequest.STATUS_PENDING);

        if (!isRepairHandler())
        {
            query.setUserId(getUserId());
        }
        return success(repairRequestService.countRepairRequest(query));
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:add')")
    @Log(title = "报修申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody RepairRequest repairRequest)
    {
        rejectHandlerUserRepair();
        repairRequest.setUserId(getUserId());
        repairRequest.setCreateBy(getUsername());
        return toAjax(repairRequestService.insertRepairRequest(repairRequest));
    }

    @PreAuthorize("@ss.hasPermi('repair:apply:cancel')")
    @Log(title = "取消报修", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel")
    public AjaxResult cancel(@RequestBody RepairRequest repairRequest)
    {
        rejectHandlerUserRepair();
        RepairRequest existing = repairRequestService.selectRepairRequestById(repairRequest.getRequestId());
        repairRequestService.checkRepairRequestOwner(existing, getUserId(), false);
        repairRequest.setUpdateBy(getUsername());
        return toAjax(repairRequestService.cancelRepairRequest(repairRequest));
    }

    @PreAuthorize("@ss.hasPermi('repair:admin:edit') or @ss.hasPermi('repair:worker:edit')")
    @Log(title = "处理报修", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody RepairRequest repairRequest)
    {
        repairRequest.setUpdateBy(getUsername());
        if (!isHandlerView())
        {
            RepairRequest existing = repairRequestService.selectRepairRequestById(repairRequest.getRequestId());
        }

        return toAjax(repairRequestService.handleRepairRequest(repairRequest));
    }
    @Log(title = "报修申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{requestIds}")
    public AjaxResult remove(@PathVariable Long[] requestIds)
    {
        if (!isHandlerView())
        {
            for (Long requestId : requestIds)
            {
                RepairRequest existing = repairRequestService.selectRepairRequestById(requestId);
                repairRequestService.checkRepairRequestOwner(existing, getUserId(), false);
            }
        }
        return toAjax(repairRequestService.deleteRepairRequestByIds(requestIds));
    }

    private boolean isHandlerView()
    {
        return isRepairHandler();
    }

    /** 是否以管理员/维修员身份查看全部用户报修 */
    private boolean isRepairHandler()
    {
        if (SecurityUtils.isAdmin() || hasRoleKey("agri_admin") || hasRoleKey("repair_worker") || hasRepairWorkerPermission())
        {
            return true;
        }

        if (hasRoleKey("common"))
        {
            return false;
        }

        return hasRepairAdminPermission();
    }

    /** 是否拥有报修管理权限 */
    private boolean hasRepairAdminPermission()
    {
        return permissionService.hasPermi("repair:admin:list")
            || permissionService.hasPermi("repair:admin:query")
            || permissionService.hasPermi("repair:admin:edit")
            || permissionService.hasPermi("repair:admin:remove");
    }

    private boolean hasRepairWorkerPermission()
    {
        return permissionService.hasPermi("repair:worker:list")
            || permissionService.hasPermi("repair:worker:query")
            || permissionService.hasPermi("repair:worker:edit")
            || permissionService.hasPermi("repair:worker:count");
    }

    private boolean hasRoleKey(String roleKey)
    {
        return getLoginUser() != null
            && getLoginUser().getUser() != null
            && getLoginUser().getUser().getRoles() != null
            && getLoginUser().getUser().getRoles().stream().anyMatch(role -> roleKey.equals(role.getRoleKey()));
    }

    /** 管理员/维修员不可使用用户端提交/取消 */
    private void rejectHandlerUserRepair()
    {
        if (!isRepairHandler())
        {
            return;
        }

        if (SecurityUtils.isAdmin())
        {
            throw new ServiceException("管理员请使用「报修管理」处理用户报修");
        }
        if (hasRoleKey("repair_worker") || hasRepairWorkerPermission())
        {
            throw new ServiceException("维修员请使用「维修任务」处理用户报修");
        }
        if (hasRoleKey("agri_admin") || hasRepairAdminPermission())
        {
            throw new ServiceException("请使用「报修管理」处理用户报修");
        }
    }
}

