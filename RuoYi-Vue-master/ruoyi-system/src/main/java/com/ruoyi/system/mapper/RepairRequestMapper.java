package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.RepairRequest;

/**
 * �������� Mapper
 */
public interface RepairRequestMapper
{
    RepairRequest selectRepairRequestById(Long requestId);

    List<RepairRequest> selectRepairRequestList(RepairRequest repairRequest);

    int insertRepairRequest(RepairRequest repairRequest);

    int updateRepairRequest(RepairRequest repairRequest);

    int deleteRepairRequestById(Long requestId);

    int deleteRepairRequestByIds(Long[] requestIds);

    // ====== 维修人员专用 ======
    List<RepairRequest> selectTechnicianPool(RepairRequest repairRequest);
    List<RepairRequest> selectTechnicianAssigned(RepairRequest repairRequest);
    List<RepairRequest> selectTechnicianHistory(RepairRequest repairRequest);
    int acceptRepairRequest(RepairRequest repairRequest);
    int startRepairRequest(RepairRequest repairRequest);
    int completeRepairRequest(RepairRequest repairRequest);
}
