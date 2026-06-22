package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.RepairRequest;
import com.ruoyi.system.mapper.RepairRequestMapper;
import com.ruoyi.system.service.IRepairRequestService;

@Service
public class RepairRequestServiceImpl implements IRepairRequestService
{
    @Autowired
    private RepairRequestMapper repairRequestMapper;

    @Override
    public RepairRequest selectRepairRequestById(Long requestId)
    {
        return repairRequestMapper.selectRepairRequestById(requestId);
    }

    @Override
    public List<RepairRequest> selectRepairRequestList(RepairRequest repairRequest)
    {
        return repairRequestMapper.selectRepairRequestList(repairRequest);
    }

    @Override
    public int insertRepairRequest(RepairRequest repairRequest)
    {
        repairRequest.setStatus(RepairRequest.STATUS_PENDING);
        return repairRequestMapper.insertRepairRequest(repairRequest);
    }

    @Override
    public int updateRepairRequest(RepairRequest repairRequest)
    {
        return repairRequestMapper.updateRepairRequest(repairRequest);
    }

    @Override
    public int cancelRepairRequest(RepairRequest repairRequest)
    {
        RepairRequest existing = repairRequestMapper.selectRepairRequestById(repairRequest.getRequestId());
        if (existing == null)
        {
            throw new ServiceException("报修记录不存在");
        }
        if (!RepairRequest.STATUS_PENDING.equals(existing.getStatus()))
        {
            throw new ServiceException("仅待受理状态的报修可以取消");
        }
        RepairRequest update = new RepairRequest();
        update.setRequestId(repairRequest.getRequestId());
        update.setStatus(RepairRequest.STATUS_CANCELLED);
        update.setUpdateBy(repairRequest.getUpdateBy());
        return repairRequestMapper.updateRepairRequest(update);
    }

    @Override
    public int handleRepairRequest(RepairRequest repairRequest)
    {
        RepairRequest existing = repairRequestMapper.selectRepairRequestById(repairRequest.getRequestId());
        if (existing == null)
        {
            throw new ServiceException("报修记录不存在");
        }
        if (RepairRequest.STATUS_CANCELLED.equals(existing.getStatus()))
        {
            throw new ServiceException("已取消的报修无法处理");
        }
        RepairRequest update = new RepairRequest();
        update.setRequestId(repairRequest.getRequestId());
        update.setStatus(repairRequest.getStatus());
        update.setAdminReply(repairRequest.getAdminReply());
        update.setRemark(repairRequest.getRemark());
        update.setUpdateBy(repairRequest.getUpdateBy());
        return repairRequestMapper.updateRepairRequest(update);
    }

    @Override
    public int deleteRepairRequestByIds(Long[] requestIds)
    {
        return repairRequestMapper.deleteRepairRequestByIds(requestIds);
    }

    @Override
    public int countRepairRequest(RepairRequest repairRequest)
    {
        return repairRequestMapper.countRepairRequest(repairRequest);
    }

    @Override
    public void checkRepairRequestOwner(RepairRequest repairRequest, Long currentUserId, boolean admin)
    {
        if (admin || repairRequest == null)
        {
            return;
        }
        if (!currentUserId.equals(repairRequest.getUserId()))
        {
            throw new ServiceException("无权访问该报修记录");
        }
    }

    // ====== 维修人员专用 ======

    @Override
    public List<RepairRequest> selectTechnicianPool(RepairRequest repairRequest)
    {
        return repairRequestMapper.selectTechnicianPool(repairRequest);
    }

    @Override
    public List<RepairRequest> selectTechnicianAssigned(RepairRequest repairRequest)
    {
        return repairRequestMapper.selectTechnicianAssigned(repairRequest);
    }

    @Override
    public List<RepairRequest> selectTechnicianHistory(RepairRequest repairRequest)
    {
        return repairRequestMapper.selectTechnicianHistory(repairRequest);
    }

    @Override
    public int acceptRepairRequest(RepairRequest repairRequest)
    {
        return repairRequestMapper.acceptRepairRequest(repairRequest);
    }

    @Override
    public int startRepairRequest(RepairRequest repairRequest)
    {
        RepairRequest existing = repairRequestMapper.selectRepairRequestById(repairRequest.getRequestId());
        if (existing == null)
        {
            throw new ServiceException("工单不存在");
        }
        if (!RepairRequest.STATUS_PROCESSING.equals(existing.getStatus()))
        {
            throw new ServiceException("仅处理中的工单可以开始维修");
        }
        if (!repairRequest.getTechnicianId().equals(existing.getTechnicianId()))
        {
            throw new ServiceException("无权操作该工单");
        }
        return repairRequestMapper.startRepairRequest(repairRequest);
    }

    @Override
    public int completeRepairRequest(RepairRequest repairRequest)
    {
        RepairRequest existing = repairRequestMapper.selectRepairRequestById(repairRequest.getRequestId());
        if (existing == null)
        {
            throw new ServiceException("工单不存在");
        }
        if (!RepairRequest.STATUS_PROCESSING.equals(existing.getStatus()))
        {
            throw new ServiceException("仅处理中的工单可以完成");
        }
        if (!repairRequest.getTechnicianId().equals(existing.getTechnicianId()))
        {
            throw new ServiceException("无权操作该工单");
        }
        return repairRequestMapper.completeRepairRequest(repairRequest);
    }

    @Override
    public void checkTechnicianOwner(RepairRequest repairRequest, Long currentUserId)
    {
        if (repairRequest == null || !currentUserId.equals(repairRequest.getTechnicianId()))
        {
            throw new ServiceException("无权操作该工单");
        }
    }
}
