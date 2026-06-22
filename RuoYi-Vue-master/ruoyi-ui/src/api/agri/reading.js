import request from '@/utils/request'
import { getWeatherLocation } from '@/utils/agriWeatherLocation'

export function listAgriReading(query) {
  return request({
    url: '/agri/reading/list',
    method: 'get',
    params: query
  })
}

export function getLatestReading(nodeId) {
  return request({
    url: '/agri/reading/latest/' + nodeId,
    method: 'get'
  })
}

export function getTrend(nodeId, hours) {
  return request({
    url: '/agri/reading/trend/' + nodeId,
    method: 'get',
    params: { hours }
  })
}

export function getAdvice(nodeId, hours, weatherLocation) {
  const location = weatherLocation || getWeatherLocation()
  return request({
    url: '/agri/reading/advice/' + nodeId,
    method: 'get',
    params: {
      hours,
      refLat: location.latitude,
      refLon: location.longitude
    }
  })
}

export function ingestReading(data) {
  return request({
    url: '/agri/reading/ingest',
    method: 'post',
    data: data
  })
}

export function simulateReading(nodeId) {
  return request({
    url: '/agri/reading/simulate/' + nodeId,
    method: 'post'
  })
}

export function delReading(readingIds) {
  return request({
    url: '/agri/reading/' + readingIds,
    method: 'delete'
  })
}
