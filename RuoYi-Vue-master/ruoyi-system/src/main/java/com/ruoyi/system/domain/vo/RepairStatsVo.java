package com.ruoyi.system.domain.vo;

/**
 * 维修统计数据（状态分布/人员/费用）
 */
public class RepairStatsVo {
    private String label;       // 标签（状态名/人员名/月份）
    private String code;        // 编码
    private Long count;         // 数量
    private java.math.BigDecimal totalCost;  // 总费用（仅人员/趋势统计）

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
    public java.math.BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(java.math.BigDecimal totalCost) { this.totalCost = totalCost; }
}
