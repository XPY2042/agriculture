package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.AgriRemoteCommand;

public interface IAgriRemoteCommandService
{
    List<AgriRemoteCommand> selectAgriRemoteCommandList(AgriRemoteCommand command);

    AgriRemoteCommand selectAgriRemoteCommandById(Long commandId);

    Map<String, String> getCommandTypeLabels();

    AgriRemoteCommand sendRemoteCommand(Long nodeId, String commandType, String operator);
}
