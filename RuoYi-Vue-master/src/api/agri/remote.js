import request from '@/utils/request'

export function listRemoteCommand(query) {
  return request({
    url: '/agri/remote/command/list',
    method: 'get',
    params: query
  })
}

export function sendRemoteCommand(data) {
  return request({
    url: '/agri/remote/command/send',
    method: 'post',
    data
  })
}

export function getRemoteCommandTypes() {
  return request({
    url: '/agri/remote/command/types',
    method: 'get'
  })
}
