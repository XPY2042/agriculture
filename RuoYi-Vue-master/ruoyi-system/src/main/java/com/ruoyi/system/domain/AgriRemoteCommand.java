package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Զ���豸����ָ�� agri_remote_command
 */
public class AgriRemoteCommand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long commandId;
    private Long nodeId;
    private String nodeName;
    private String commandType;
    private String commandLabel;
    /** 0��ִ�� 1�ɹ� 2ʧ�� */
    private String status;
    private String resultMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    public Long getCommandId()
    {
        return commandId;
    }

    public void setCommandId(Long commandId)
    {
        this.commandId = commandId;
    }

    public Long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getCommandType()
    {
        return commandType;
    }

    public void setCommandType(String commandType)
    {
        this.commandType = commandType;
    }

    public String getCommandLabel()
    {
        return commandLabel;
    }

    public void setCommandLabel(String commandLabel)
    {
        this.commandLabel = commandLabel;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getResultMessage()
    {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public Date getExecuteTime()
    {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime)
    {
        this.executeTime = executeTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("commandId", getCommandId())
            .append("nodeId", getNodeId())
            .append("commandType", getCommandType())
            .append("commandLabel", getCommandLabel())
            .append("status", getStatus())
            .append("resultMessage", getResultMessage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("executeTime", getExecuteTime())
            .toString();
    }
}
