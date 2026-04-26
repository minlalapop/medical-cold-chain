import api from './api'

export const alertService = {
  getAll: () => api.get('/api/alerts'),
  getOpen: () => api.get('/api/alerts/open'),
  checkAll: () => api.post('/api/alerts/check-all'),
  checkExpiry: () => api.post('/api/alerts/check-expiry'),
  checkTemperature: () => api.post('/api/alerts/check-temperature'),
  resolve: (id, payload) => api.put(`/api/alerts/${id}/resolve`, payload),
}
