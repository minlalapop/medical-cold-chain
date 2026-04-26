import api from './api'

export const auditService = {
  getAll: () => api.get('/api/audit-logs'),
  create: (payload) => api.post('/api/audit-logs', payload),
  byService: (serviceName) => api.get(`/api/audit-logs/service/${serviceName}`),
  bySeverity: (severity) => api.get(`/api/audit-logs/severity/${severity}`),
}
