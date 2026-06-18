package com.ruoyi.system.domain.vo;

/**
 * 告警统计分布结果
 */
public class AlarmStatsVo {
    private String name;      // 指标名称或级别
    private String code;      // 指标编码或级别值
    private Long count;       // 告警数量

    public AlarmStatsVo() {}
    public AlarmStatsVo(String name, String code, Long count) {
        this.name = name; this.code = code; this.count = count;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
