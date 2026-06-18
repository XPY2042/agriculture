package com.ruoyi.web.controller.agri;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.service.IAgriSensorNodeService;

/**
 * 农业物联网传感节点
 */
@RestController
@RequestMapping("/agri/node")
public class AgriSensorNodeController extends BaseController
{
    @Autowired
    private IAgriSensorNodeService agriSensorNodeService;

    @PreAuthorize("@ss.hasPermi('agri:node:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriSensorNode node)
    {
        if (SecurityUtils.isAdmin())
        {
            if (StringUtils.isEmpty(node.getCreateBy()))
            {
                return getDataTable(Collections.emptyList());
            }
        }
        else
        {
            node.setCreateBy(getUsername());
        }
        startPage();
        List<AgriSensorNode> list = agriSensorNodeService.selectAgriSensorNodeList(node);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('agri:node:query')")
    @GetMapping(value = "/{nodeId}")
    public AjaxResult getInfo(@PathVariable Long nodeId)
    {
        AgriSensorNode node = agriSensorNodeService.selectAgriSensorNodeById(nodeId);
        agriSensorNodeService.checkAgriSensorNodeOwner(node, getUsername(), SecurityUtils.isAdmin());
        return success(node);
    }

    @PreAuthorize("@ss.hasPermi('agri:node:add')")
    @Log(title = "传感节点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AgriSensorNode node)
    {
        if (SecurityUtils.isAdmin())
        {
            if (StringUtils.isEmpty(node.getCreateBy()))
            {
                return error("请先选择所属用户");
            }
        }
        else
        {
            node.setCreateBy(getUsername());
        }
        if (!agriSensorNodeService.checkNodeCodeUnique(node))
        {
            return error("新增失败，设备编码已存在");
        }
        return toAjax(agriSensorNodeService.insertAgriSensorNode(node));
    }

    @PreAuthorize("@ss.hasPermi('agri:node:edit')")
    @Log(title = "传感节点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AgriSensorNode node)
    {
        AgriSensorNode existing = agriSensorNodeService.selectAgriSensorNodeById(node.getNodeId());
        agriSensorNodeService.checkAgriSensorNodeOwner(existing, getUsername(), SecurityUtils.isAdmin());
        if (!agriSensorNodeService.checkNodeCodeUnique(node))
        {
            return error("修改失败，设备编码已存在");
        }
        node.setUpdateBy(getUsername());
        return toAjax(agriSensorNodeService.updateAgriSensorNode(node));
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/heatmap")
    public AjaxResult heatmap(AgriSensorNode node)
    {
        if (!SecurityUtils.isAdmin())
        {
            node.setCreateBy(getUsername());
        }
        List<AgriSensorNode> list = agriSensorNodeService.selectNodeHeatmap(node);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('agri:node:remove')")
    @Log(title = "传感节点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{nodeIds}")
    public AjaxResult remove(@PathVariable Long[] nodeIds)
    {
        for (Long nodeId : nodeIds)
        {
            AgriSensorNode existing = agriSensorNodeService.selectAgriSensorNodeById(nodeId);
            agriSensorNodeService.checkAgriSensorNodeOwner(existing, getUsername(), SecurityUtils.isAdmin());
        }
        return toAjax(agriSensorNodeService.deleteAgriSensorNodeByIds(nodeIds));
    }

    @PreAuthorize("@ss.hasPermi('agri:node:list') or @ss.hasPermi('agri:node:query')")
    @Log(title = "传感节点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriSensorNode node)
    {
        List<AgriSensorNode> list = agriSensorNodeService.selectAgriSensorNodeList(node);
        ExcelUtil<AgriSensorNode> util = new ExcelUtil<AgriSensorNode>(AgriSensorNode.class);
        util.exportExcel(response, list, "传感节点");
    }
}
