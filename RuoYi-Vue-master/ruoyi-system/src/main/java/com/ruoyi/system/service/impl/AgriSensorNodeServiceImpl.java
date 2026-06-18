package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.mapper.AgriSensorNodeMapper;
import com.ruoyi.system.service.IAgriSensorNodeService;

@Service
public class AgriSensorNodeServiceImpl implements IAgriSensorNodeService
{
    @Autowired
    private AgriSensorNodeMapper agriSensorNodeMapper;

    @Override
    public AgriSensorNode selectAgriSensorNodeById(Long nodeId)
    {
        return agriSensorNodeMapper.selectAgriSensorNodeById(nodeId);
    }

    @Override
    public List<AgriSensorNode> selectAgriSensorNodeList(AgriSensorNode node)
    {
        return agriSensorNodeMapper.selectAgriSensorNodeList(node);
    }

    @Override
    public int insertAgriSensorNode(AgriSensorNode node)
    {
        if (StringUtils.isEmpty(node.getStatus()))
        {
            node.setStatus("0");
        }
        return agriSensorNodeMapper.insertAgriSensorNode(node);
    }

    @Override
    public int updateAgriSensorNode(AgriSensorNode node)
    {
        return agriSensorNodeMapper.updateAgriSensorNode(node);
    }

    @Override
    public int deleteAgriSensorNodeByIds(Long[] nodeIds)
    {
        return agriSensorNodeMapper.deleteAgriSensorNodeByIds(nodeIds);
    }

    @Override
    public boolean checkNodeCodeUnique(AgriSensorNode node)
    {
        Long nodeId = StringUtils.isNull(node.getNodeId()) ? -1L : node.getNodeId();
        AgriSensorNode info = agriSensorNodeMapper.checkNodeCodeUnique(node.getNodeCode());
        if (StringUtils.isNotNull(info) && !info.getNodeId().equals(nodeId))
        {
            return false;
        }
        return true;
    }

    @Override
    public void checkAgriSensorNodeOwner(AgriSensorNode node, String currentUsername, boolean admin)
    {
        if (admin || node == null)
        {
            return;
        }
        if (!StringUtils.equals(currentUsername, node.getCreateBy()))
        {
            throw new ServiceException("无权访问该传感节点");
        }
    }

    @Override
    public List<AgriSensorNode> selectNodeHeatmap(AgriSensorNode node)
    {
        if (node == null)
        {
            node = new AgriSensorNode();
        }
        return agriSensorNodeMapper.selectNodeHeatmap(node);
    }
}
