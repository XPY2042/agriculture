import request from '@/utils/request'

export function listAgriNode(query) {
  return request({
    url: '/agri/node/list',
    method: 'get',
    params: query
  })
}

export function getAgriNode(nodeId) {
  return request({
    url: '/agri/node/' + nodeId,
    method: 'get'
  })
}

export function addAgriNode(data) {
  return request({
    url: '/agri/node',
    method: 'post',
    data: data
  })
}

export function updateAgriNode(data) {
  return request({
    url: '/agri/node',
    method: 'put',
    data: data
  })
}

export function delAgriNode(nodeId) {
  return request({
    url: '/agri/node/' + nodeId,
    method: 'delete'
  })
}

export function getNodeHeatmap(query) {
  return request({
    url: '/agri/node/heatmap',
    method: 'get',
    params: query
  })
}
