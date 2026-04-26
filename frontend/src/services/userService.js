import api from './api'

export const userService = {
  getAll: () => api.get('/api/users'),
  create: (payload) => api.post('/api/users', payload),
  update: (id, payload) => api.put(`/api/users/${id}`, payload),
  activate: (id) => api.put(`/api/users/${id}/activate`),
  deactivate: (id) => api.put(`/api/users/${id}/deactivate`),
  delete: (id) => api.delete(`/api/users/${id}`),
}
