package com.ruoyi.web.controller.agri;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AgriRemoteCommand;
import com.ruoyi.system.service.IAgriRemoteCommandService;

@RestController
@RequestMapping("/agri/remote")
public class AgriRemoteController extends BaseController
{
    @Autowired
    private IAgriRemoteCommandService agriRemoteCommandService;

    @PreAuthorize("@ss.hasPermi('agri:remote:view')")
    @GetMapping("/command/list")
    public TableDataInfo list(AgriRemoteCommand command)
    {
        startPage();
        List<AgriRemoteCommand> list = agriRemoteCommandService.selectAgriRemoteCommandList(command);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('agri:remote:view')")
    @GetMapping("/command/{commandId}")
    public AjaxResult getInfo(@PathVariable Long commandId)
    {
        return success(agriRemoteCommandService.selectAgriRemoteCommandById(commandId));
    }

    @PreAuthorize("@ss.hasPermi('agri:remote:operate')")
    @Log(title = "远程设备操作", businessType = BusinessType.OTHER)
    @PostMapping("/command/send")
    public AjaxResult send(@RequestBody Map<String, Object> body)
    {
        Object nodeIdObj = body.get("nodeId");
        Object typeObj = body.get("commandType");
        if (nodeIdObj == null || typeObj == null)
        {
            return error("节点与指令类型不能为空");
        }
        Long nodeId = Long.valueOf(nodeIdObj.toString());
        String commandType = typeObj.toString();
        AgriRemoteCommand cmd = agriRemoteCommandService.sendRemoteCommand(nodeId, commandType, getUsername());
        return success(cmd);
    }

    @PreAuthorize("@ss.hasPermi('agri:remote:view')")
    @GetMapping("/command/types")
    public AjaxResult commandTypes()
    {
        return success(agriRemoteCommandService.getCommandTypeLabels());
    }
}
