package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AgriSensorNode;

/**
 * ũҵ���нڵ� Mapper
 */
public interface AgriSensorNodeMapper
{
    AgriSensorNode selectAgriSensorNodeById(Long nodeId);

    List<AgriSensorNode> selectAgriSensorNodeList(AgriSensorNode node);

    int insertAgriSensorNode(AgriSensorNode node);

    int updateAgriSensorNode(AgriSensorNode node);

    int deleteAgriSensorNodeById(Long nodeId);

    int deleteAgriSensorNodeByIds(Long[] nodeIds);

    AgriSensorNode checkNodeCodeUnique(String nodeCode);
}
