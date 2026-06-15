package com.ruoyi.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * ũҵ���������нڵ� agri_sensor_node
 */
public class AgriSensorNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long nodeId;

    @NotBlank(message = "�豸���벻��Ϊ��")
    @Size(max = 64, message = "�豸���벻�ܳ���64���ַ�")
    private String nodeCode;

    @NotBlank(message = "�ڵ����Ʋ���Ϊ��")
    @Size(max = 100, message = "�ڵ����Ʋ��ܳ���100���ַ�")
    private String nodeName;

    @Size(max = 200, message = "λ���������ܳ���200���ַ�")
    private String plotLocation;

    @Size(max = 64, message = "�������Ͳ��ܳ���64���ַ�")
    private String cropType;

    private String status;

    private String delFlag;

    /** 热力图：未处理告警数 */
    private Integer alarmCount;
    /** 热力图：最高告警级别 1=警告 2=危险 */
    private String maxAlarmLevel;

    public Long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeCode()
    {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode)
    {
        this.nodeCode = nodeCode;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getPlotLocation()
    {
        return plotLocation;
    }

    public void setPlotLocation(String plotLocation)
    {
        this.plotLocation = plotLocation;
    }

    public String getCropType()
    {
        return cropType;
    }

    public void setCropType(String cropType)
    {
        this.cropType = cropType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getAlarmCount()
    {
        return alarmCount;
    }

    public void setAlarmCount(Integer alarmCount)
    {
        this.alarmCount = alarmCount;
    }

    public String getMaxAlarmLevel()
    {
        return maxAlarmLevel;
    }

    public void setMaxAlarmLevel(String maxAlarmLevel)
    {
        this.maxAlarmLevel = maxAlarmLevel;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("nodeId", getNodeId())
            .append("nodeCode", getNodeCode())
            .append("nodeName", getNodeName())
            .append("plotLocation", getPlotLocation())
            .append("cropType", getCropType())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
