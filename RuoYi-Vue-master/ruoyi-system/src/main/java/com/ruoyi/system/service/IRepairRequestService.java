package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RepairRequest;

/**
 * ��������
 */
public interface IRepairRequestService
{
    RepairRequest selectRepairRequestById(Long requestId);

    List<RepairRequest> selectRepairRequestList(RepairRequest repairRequest);

    int insertRepairRequest(RepairRequest repairRequest);

    int updateRepairRequest(RepairRequest repairRequest);

    int cancelRepairRequest(RepairRequest repairRequest);

    int handleRepairRequest(RepairRequest repairRequest);

    int deleteRepairRequestByIds(Long[] requestIds);

    int countRepairRequest(RepairRequest repairRequest);

    void checkRepairRequestOwner(RepairRequest repairRequest, Long currentUserId, boolean admin);

    // ====== 维修人员专用 ======
    List<RepairRequest> selectTechnicianPool(RepairRequest repairRequest);
    List<RepairRequest> selectTechnicianAssigned(RepairRequest repairRequest);
    List<RepairRequest> selectTechnicianHistory(RepairRequest repairRequest);
    int acceptRepairRequest(RepairRequest repairRequest);
    int startRepairRequest(RepairRequest repairRequest);
    int completeRepairRequest(RepairRequest repairRequest);
    void checkTechnicianOwner(RepairRequest repairRequest, Long currentUserId);
}
