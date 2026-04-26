import api from './api'

export const temperatureService = {
  getReadings: () => api.get('/api/temperatures/readings'),
  createReading: (payload) => api.post('/api/temperatures/readings', payload),
  getIncidents: () => api.get('/api/temperatures/incidents'),
  getOpenIncidents: () => api.get('/api/temperatures/incidents/open'),
  resolveIncident: (id, payload) => api.put(`/api/temperatures/incidents/${id}/resolve`, payload),
}
