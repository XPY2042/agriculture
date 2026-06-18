import request from '@/utils/request'

export function listAgriAlarm(query) {
  return request({
    url: '/agri/alarm/list',
    method: 'get',
    params: query
  })
}

export function getAgriAlarm(alarmId) {
  return request({
    url: '/agri/alarm/' + alarmId,
    method: 'get'
  })
}

export function getUnhandledAlarmCount() {
  return request({
    url: '/agri/alarm/unhandled/count',
    method: 'get'
  })
}

export function confirmAlarms(alarmIds) {
  return request({
    url: '/agri/alarm/confirm',
    method: 'put',
    data: alarmIds
  })
}

export function scanAlarms() {
  return request({
    url: '/agri/alarm/scan',
    method: 'put'
  })
}
