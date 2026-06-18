import request from '@/utils/request'

// 告警按指标/级别分布 type=metric|level
export function alarmDistribution(type) {
  return request({ url: '/agri/statistics/alarm/distribution', method: 'get', params: { type } })
}

// 告警趋势 days=30
export function alarmTrend(days) {
  return request({ url: '/agri/statistics/alarm/trend', method: 'get', params: { days } })
}

// 各节点读数频率
export function nodeReadingCount() {
  return request({ url: '/agri/statistics/node/count', method: 'get' })
}

// 节点日均值
export function readingDaily(nodeId, days) {
  return request({ url: '/agri/statistics/reading/daily', method: 'get', params: { nodeId, days } })
}

// 维修工单状态分布
export function repairStatusStats() {
  return request({ url: '/repair/technician/statistics/status', method: 'get' })
}

// 维修人员工作量
export function repairTechnicianStats() {
  return request({ url: '/repair/technician/statistics/technician', method: 'get' })
}

// 维修费用趋势
export function repairCostTrend(months) {
  return request({ url: '/repair/technician/statistics/cost-trend', method: 'get', params: { months } })
}
