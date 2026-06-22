import request from '@/utils/request'

export function getReportFilterOptions() {
  return request({
    url: '/report/statistics/options',
    method: 'get'
  })
}

export function getRepairReport(params) {
  return request({
    url: '/report/statistics/repair',
    method: 'get',
    params
  })
}

export function getAlarmReport(params) {
  return request({
    url: '/report/statistics/alarm',
    method: 'get',
    params
  })
}
