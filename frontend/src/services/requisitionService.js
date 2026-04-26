import api from './api'

export const requisitionService = {
  getAll: () => api.get('/api/requisitions'),
  getPending: () => api.get('/api/requisitions/pending'),
  getDepartments: () => api.get('/api/requisitions/departments'),
  getDepartmentStats: () => api.get('/api/requisitions/stats/departments'),
  create: (payload) => api.post('/api/requisitions', payload),
  approve: (id, pharmacistName) => api.put(`/api/requisitions/${id}/approve`, { pharmacistName }),
  reject: (id, pharmacistName, reason) => api.put(`/api/requisitions/${id}/reject`, { pharmacistName, reason }),
  deliver: (id, deliveredBy) => api.put(`/api/requisitions/${id}/deliver`, { deliveredBy }),
  cancel: (id, actorName, reason) => api.put(`/api/requisitions/${id}/cancel`, { actorName, reason }),
}
