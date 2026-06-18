package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报修申请 repair_request
 */
public class RepairRequest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 0待受理 1处理中 2已完成 3已取消 */
    public static final String STATUS_PENDING = "0";
    public static final String STATUS_PROCESSING = "1";
    public static final String STATUS_DONE = "2";
    public static final String STATUS_CANCELLED = "3";

    @Excel(name = "工单编号", cellType = Excel.ColumnType.NUMERIC)
    private Long requestId;

    private Long userId;

    /** 关联大棚节点ID（可选） */
    private Long nodeId;

    @NotBlank(message = "报修原因不能为空")
    @Size(max = 100, message = "报修原因不能超过100个字符")
    private String title;

    @Excel(name = "问题描述")
    @NotBlank(message = "问题描述不能为空")
    @Size(max = 1000, message = "问题描述不能超过1000个字符")
    private String description;

    @Excel(name = "报修地点")
    @Size(max = 200, message = "报修地点不能超过200个字符")
    private String location;

    @Excel(name = "联系电话")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;

    @Excel(name = "状态", readConverterExp = "0=待受理,1=处理中,2=已完成,3=已取消")
    private String status;

    @Excel(name = "管理员回复")
    @Size(max = 500, message = "管理员回复不能超过500个字符")
    private String adminReply;

    private String delFlag;

    /** 受理维修人员ID */
    private Long technicianId;

    @Excel(name = "受理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date acceptedAt;

    @Excel(name = "维修开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date repairStartAt;

    @Excel(name = "维修完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date repairFinishAt;

    @Excel(name = "维修日志")
    private String repairLog;

    @Excel(name = "使用配件")
    private String partsUsed;

    @Excel(name = "维修费用")
    private BigDecimal repairCost;

    @Excel(name = "报修人", type = Excel.Type.EXPORT)
    private String userName;

    @Excel(name = "维修人员", type = Excel.Type.EXPORT)
    private String technicianName;

    public Long getTechnicianId() { return technicianId; }
    public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }

    public Date getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Date acceptedAt) { this.acceptedAt = acceptedAt; }

    public Date getRepairStartAt() { return repairStartAt; }
    public void setRepairStartAt(Date repairStartAt) { this.repairStartAt = repairStartAt; }

    public Date getRepairFinishAt() { return repairFinishAt; }
    public void setRepairFinishAt(Date repairFinishAt) { this.repairFinishAt = repairFinishAt; }

    public String getRepairLog() { return repairLog; }
    public void setRepairLog(String repairLog) { this.repairLog = repairLog; }

    public String getPartsUsed() { return partsUsed; }
    public void setPartsUsed(String partsUsed) { this.partsUsed = partsUsed; }

    public BigDecimal getRepairCost() { return repairCost; }
    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public Long getRequestId()
    {
        return requestId;
    }

    public void setRequestId(Long requestId)
    {
        this.requestId = requestId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getAdminReply()
    {
        return adminReply;
    }

    public void setAdminReply(String adminReply)
    {
        this.adminReply = adminReply;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("requestId", getRequestId())
            .append("userId", getUserId())
            .append("nodeId", getNodeId())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("location", getLocation())
            .append("contactPhone", getContactPhone())
            .append("status", getStatus())
            .append("adminReply", getAdminReply())
            .append("technicianId", getTechnicianId())
            .append("acceptedAt", getAcceptedAt())
            .append("repairStartAt", getRepairStartAt())
            .append("repairFinishAt", getRepairFinishAt())
            .append("repairLog", getRepairLog())
            .append("partsUsed", getPartsUsed())
            .append("repairCost", getRepairCost())
            .append("delFlag", getDelFlag())
            .append("userName", getUserName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
