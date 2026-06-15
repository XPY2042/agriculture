package com.ruoyi.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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

    private Long requestId;

    private Long userId;

    @NotBlank(message = "报修原因不能为空")
    @Size(max = 100, message = "报修原因不能超过100个字符")
    private String title;

    @NotBlank(message = "问题描述不能为空")
    @Size(max = 1000, message = "问题描述不能超过1000个字符")
    private String description;

    @Size(max = 200, message = "报修地点不能超过200个字符")
    private String location;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;

    private String status;

    @Size(max = 500, message = "管理员回复不能超过500个字符")
    private String adminReply;

    private String delFlag;

    /** 报修用户昵称（列表展示） */
    private String userName;

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
            .append("title", getTitle())
            .append("description", getDescription())
            .append("location", getLocation())
            .append("contactPhone", getContactPhone())
            .append("status", getStatus())
            .append("adminReply", getAdminReply())
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
