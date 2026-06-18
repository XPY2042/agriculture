package com.ruoyi.system.domain.vo;

/**
 * 节点读数统计
 */
public class NodeReadingStatsVo {
    private Long nodeId;
    private String nodeName;
    private Long readingCount;

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public Long getReadingCount() { return readingCount; }
    public void setReadingCount(Long readingCount) { this.readingCount = readingCount; }
}
