import api from './api'

export const inventoryService = {
  getProducts: () => api.get('/api/products'),
  createProduct: (payload) => api.post('/api/products', payload),
  updateProduct: (id, payload) => api.put(`/api/products/${id}`, payload),
  deleteProduct: (id) => api.delete(`/api/products/${id}`),

  getLocations: () => api.get('/api/locations'),
  createLocation: (payload) => api.post('/api/locations', payload),
  deleteLocation: (id) => api.delete(`/api/locations/${id}`),

  getBatches: () => api.get('/api/batches'),
  createBatch: (payload) => api.post('/api/batches', payload),
  deleteBatch: (id) => api.delete(`/api/batches/${id}`),
  getStock: (productId) => api.get(`/api/inventory/stock/${productId}`),
  getLowStock: () => api.get('/api/inventory/low-stock'),
}
