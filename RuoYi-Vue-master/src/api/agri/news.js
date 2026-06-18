import request from '@/utils/request'

export function listAgriNews(query) {
  return request({
    url: '/agri/news/list',
    method: 'get',
    params: query
  })
}

export function getAgriNews(articleId) {
  return request({
    url: '/agri/news/' + articleId,
    method: 'get'
  })
}

export function refreshAgriNews() {
  return request({
    url: '/agri/news/refresh',
    method: 'put'
  })
}
