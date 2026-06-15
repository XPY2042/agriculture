package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AgriRemoteCommand;

public interface AgriRemoteCommandMapper
{
    AgriRemoteCommand selectAgriRemoteCommandById(Long commandId);

    List<AgriRemoteCommand> selectAgriRemoteCommandList(AgriRemoteCommand command);

    int insertAgriRemoteCommand(AgriRemoteCommand command);

    int updateAgriRemoteCommand(AgriRemoteCommand command);
}
