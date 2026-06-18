/**
 * Join Vue Router paths (POSIX). Avoids Windows path.resolve producing C:\... URLs.
 */
export function joinRoutePath(basePath, routePath) {
  if (!routePath) {
    return basePath || '/'
  }
  if (routePath.startsWith('http://') || routePath.startsWith('https://')) {
    return routePath
  }
  if (routePath.startsWith('/')) {
    return routePath.replace(/\/+/g, '/')
  }
  const base = (basePath || '/').replace(/\/+$/, '')
  return (base + '/' + routePath).replace(/\/+/g, '/')
}
