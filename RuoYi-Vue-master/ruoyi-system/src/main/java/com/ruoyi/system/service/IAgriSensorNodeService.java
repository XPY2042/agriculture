package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AgriSensorNode;

/**
 * ũҵ���нڵ�
 */
public interface IAgriSensorNodeService
{
    AgriSensorNode selectAgriSensorNodeById(Long nodeId);

    List<AgriSensorNode> selectAgriSensorNodeList(AgriSensorNode node);

    int insertAgriSensorNode(AgriSensorNode node);

    int updateAgriSensorNode(AgriSensorNode node);

    int deleteAgriSensorNodeByIds(Long[] nodeIds);

    boolean checkNodeCodeUnique(AgriSensorNode node);

    /** 校验当前用户是否有权访问该节点（管理员跳过） */
    void checkAgriSensorNodeOwner(AgriSensorNode node, String currentUsername, boolean admin);

    /** 热力图：节点及告警/报修汇总 */
    List<AgriSensorNode> selectNodeHeatmap(AgriSensorNode node);
}
