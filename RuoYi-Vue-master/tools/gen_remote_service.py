# Generate AgriRemoteCommandServiceImpl.java with Unicode escapes only (ASCII-safe).
from pathlib import Path

JAVA = r"""package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.AgriRemoteCommand;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.mapper.AgriRemoteCommandMapper;
import com.ruoyi.system.mapper.AgriSensorNodeMapper;
import com.ruoyi.system.service.IAgriRemoteCommandService;

@Service
public class AgriRemoteCommandServiceImpl implements IAgriRemoteCommandService
{
    private static final Map<String, String> COMMAND_LABELS = new LinkedHashMap<>();

    static
    {
        COMMAND_LABELS.put("IRRIGATE_ON", "\u5f00\u542f\u704c\u6e89");
        COMMAND_LABELS.put("IRRIGATE_OFF", "\u5173\u95ed\u704c\u6e89");
        COMMAND_LABELS.put("FAN_ON", "\u5f00\u542f\u901a\u98ce");
        COMMAND_LABELS.put("FAN_OFF", "\u5173\u95ed\u901a\u98ce");
        COMMAND_LABELS.put("READ_SENSOR", "\u89e6\u53d1\u4f20\u611f\u8bfb\u6570");
        COMMAND_LABELS.put("REBOOT", "\u91cd\u542f\u8282\u70b9");
    }

    @Autowired
    private AgriRemoteCommandMapper agriRemoteCommandMapper;

    @Autowired
    private AgriSensorNodeMapper agriSensorNodeMapper;

    @Override
    public List<AgriRemoteCommand> selectAgriRemoteCommandList(AgriRemoteCommand command)
    {
        return agriRemoteCommandMapper.selectAgriRemoteCommandList(command);
    }

    @Override
    public AgriRemoteCommand selectAgriRemoteCommandById(Long commandId)
    {
        return agriRemoteCommandMapper.selectAgriRemoteCommandById(commandId);
    }

    @Override
    public Map<String, String> getCommandTypeLabels()
    {
        return new LinkedHashMap<>(COMMAND_LABELS);
    }

    @Override
    public AgriRemoteCommand sendRemoteCommand(Long nodeId, String commandType, String operator)
    {
        if (nodeId == null)
        {
            throw new ServiceException("\u8bf7\u9009\u62e9\u4f20\u611f\u8282\u70b9");
        }
        String type = commandType == null ? "" : commandType.trim().toUpperCase();
        if (!COMMAND_LABELS.containsKey(type))
        {
            throw new ServiceException("\u4e0d\u652f\u6301\u7684\u8fdc\u7a0b\u6307\u4ee4\u7c7b\u578b");
        }
        AgriSensorNode node = agriSensorNodeMapper.selectAgriSensorNodeById(nodeId);
        if (node == null)
        {
            throw new ServiceException("\u4f20\u611f\u8282\u70b9\u4e0d\u5b58\u5728");
        }
        if (!"0".equals(node.getStatus()))
        {
            throw new ServiceException("\u8282\u70b9\u5df2\u505c\u7528\uff0c\u65e0\u6cd5\u8fdc\u7a0b\u64cd\u4f5c");
        }

        Date now = new Date();
        AgriRemoteCommand cmd = new AgriRemoteCommand();
        cmd.setNodeId(nodeId);
        cmd.setCommandType(type);
        cmd.setCommandLabel(COMMAND_LABELS.get(type));
        cmd.setStatus("1");
        cmd.setResultMessage("\u6307\u4ee4\u5df2\u901a\u8fc7\u5e73\u53f0\u4e0b\u53d1\u81f3\u8bbe\u5907 "
            + node.getNodeCode() + "\uff08\u6f14\u793a\u6a21\u5f0f\uff1a\u672c\u5730\u6a21\u62df\u6210\u529f\uff09");
        cmd.setCreateBy(operator);
        cmd.setCreateTime(now);
        cmd.setExecuteTime(now);
        agriRemoteCommandMapper.insertAgriRemoteCommand(cmd);
        cmd.setNodeName(node.getNodeName());
        return cmd;
    }
}
"""

if __name__ == "__main__":
    out = Path(__file__).resolve().parents[1] / "ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AgriRemoteCommandServiceImpl.java"
    out.write_text(JAVA, encoding="ascii")
    print("wrote", out)
