const STORAGE_KEY = 'agri_weather_location'

export const DEFAULT_WEATHER_LOCATION = {
  latitude: 39.9042,
  longitude: 116.4074
}

function normalizeNumber(value, min, max) {
  const n = Number(value)
  if (!Number.isFinite(n) || n < min || n > max) {
    return null
  }
  return n
}

export function normalizeWeatherLocation(latitude, longitude) {
  const lat = normalizeNumber(latitude, -90, 90)
  const lon = normalizeNumber(longitude, -180, 180)
  if (lat === null || lon === null) {
    return null
  }
  return { latitude: lat, longitude: lon }
}

export function getWeatherLocation() {
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const saved = JSON.parse(raw)
      const normalized = normalizeWeatherLocation(saved.latitude, saved.longitude)
      if (normalized) {
        return normalized
      }
    }
  } catch (e) {
    // Ignore broken local storage values and fall back to defaults.
  }
  return { ...DEFAULT_WEATHER_LOCATION }
}

export function saveWeatherLocation(latitude, longitude) {
  const normalized = normalizeWeatherLocation(latitude, longitude)
  if (!normalized) {
    return null
  }
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(normalized))
  return normalized
}

export function buildOpenMeteoCurrentUrl(latitude, longitude) {
  const location = normalizeWeatherLocation(latitude, longitude) || getWeatherLocation()
  return 'https://api.open-meteo.com/v1/forecast?latitude=' + location.latitude.toFixed(4) +
    '&longitude=' + location.longitude.toFixed(4) +
    '&current=temperature_2m,relative_humidity_2m'
}

export function latitudeDirection(latitude) {
  return Number(latitude) < 0 ? 'S' : 'N'
}

export function longitudeDirection(longitude) {
  return Number(longitude) < 0 ? 'W' : 'E'
}

export function formatWeatherCoordinate(latitude, longitude) {
  const location = normalizeWeatherLocation(latitude, longitude)
  if (!location) {
    return '--'
  }
  return Math.abs(location.latitude).toFixed(4) + latitudeDirection(location.latitude) +
    ', ' + Math.abs(location.longitude).toFixed(4) + longitudeDirection(location.longitude)
}
