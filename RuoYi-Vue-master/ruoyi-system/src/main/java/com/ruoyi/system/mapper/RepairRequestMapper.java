package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.RepairRequest;
import com.ruoyi.system.domain.vo.RepairStatsVo;

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

    /** 按状态统计工单数量 */
    List<RepairStatsVo> countByStatus();

    /** 维修人员工作量统计 */
    List<RepairStatsVo> techStats();

    /** 按月统计维修费用趋势 */
    List<RepairStatsVo> costTrendByMonth(@Param("months") int months);
}
