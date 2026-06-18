package com.ruoyi.system.domain.vo;

/**
 * 告警时间趋势
 */
public class AlarmTrendVo {
    private String dateStr;   // 日期字符串 yyyy-MM-dd
    private Long alarmCount;  // 当日告警数

    public String getDateStr() { return dateStr; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }
    public Long getAlarmCount() { return alarmCount; }
    public void setAlarmCount(Long alarmCount) { this.alarmCount = alarmCount; }
}
