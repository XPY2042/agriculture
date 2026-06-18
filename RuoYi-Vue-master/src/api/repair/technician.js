import request from '@/utils/request'

// 工单池列表（所有待受理报修 Status=0）
export function listTicketPool(query) {
  return request({
    url: '/repair/technician/pool',
    method: 'get',
    params: query
  })
}

// 受理工单
export function acceptTicket(requestId) {
  return request({
    url: '/repair/technician/accept/' + requestId,
    method: 'put'
  })
}

// 我的工单列表
export function listMyTickets(query) {
  return request({
    url: '/repair/technician/my-tickets',
    method: 'get',
    params: query
  })
}

// 开始维修
export function startRepair(requestId) {
  return request({
    url: '/repair/technician/start/' + requestId,
    method: 'put'
  })
}

// 完成维修（含维修日志/配件/费用）
export function completeRepair(data) {
  return request({
    url: '/repair/technician/complete',
    method: 'put',
    data: data
  })
}

// 维修历史
export function listRepairHistory(query) {
  return request({
    url: '/repair/technician/history',
    method: 'get',
    params: query
  })
}

// 工单详情
export function getTicketDetail(requestId) {
  return request({
    url: '/repair/technician/' + requestId,
    method: 'get'
  })
}

// 导出维修记录（直接用 download 工具函数，无需单独 API 包装 - 见 history/index.vue）
