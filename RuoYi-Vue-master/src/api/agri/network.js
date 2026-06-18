import request from '@/utils/request'

export function getNetworkStatus() {
  return request({
    url: '/agri/network/status',
    method: 'get'
  })
}

export function pingNetwork() {
  return request({
    url: '/agri/network/ping',
    method: 'get'
  })
}

export function fetchNetworkUrl(url) {
  return request({
    url: '/agri/network/fetch',
    method: 'get',
    params: { url }
  })
}

export function getOpenMeteoWeather(latitude, longitude) {
  return request({
    url: '/agri/network/weather',
    method: 'get',
    params: { latitude, longitude }
  })
}
