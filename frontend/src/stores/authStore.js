import { defineStore } from 'pinia'
import { authService } from '../services/authService'

const savedUser = localStorage.getItem('user')

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: savedUser ? JSON.parse(savedUser) : null,
    loading: false,
    error: '',
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
    role: (state) => state.user?.role || '',
    displayName: (state) => state.user?.fullName || state.user?.email || 'User',
  },
  actions: {
    persist(response) {
      const user = {
        userId: response.userId,
        fullName: response.fullName,
        email: response.email,
        role: response.role,
      }
      this.token = response.token
      this.user = user
      localStorage.setItem('token', response.token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    async login(payload) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await authService.login(payload)
        this.persist(data)
      } catch (error) {
        this.error = error.response?.data?.error || 'Login failed'
        throw error
      } finally {
        this.loading = false
      }
    },
    async register(payload) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await authService.register(payload)
        this.persist(data)
      } catch (error) {
        this.error = error.response?.data?.error || 'Register failed'
        throw error
      } finally {
        this.loading = false
      }
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})
