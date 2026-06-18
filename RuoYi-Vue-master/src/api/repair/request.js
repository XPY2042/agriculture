import request from '@/utils/request'

export function listRepairRequest(query) {
  return request({
    url: '/repair/request/list',
    method: 'get',
    params: query
  })
}

export function getRepairRequest(requestId) {
  return request({
    url: '/repair/request/' + requestId,
    method: 'get'
  })
}

export function addRepairRequest(data) {
  return request({
    url: '/repair/request',
    method: 'post',
    data: data
  })
}

export function cancelRepairRequest(data) {
  return request({
    url: '/repair/request/cancel',
    method: 'put',
    data: data
  })
}

export function handleRepairRequest(data) {
  return request({
    url: '/repair/request/handle',
    method: 'put',
    data: data
  })
}

export function delRepairRequest(requestId) {
  return request({
    url: '/repair/request/' + requestId,
    method: 'delete'
  })
}
