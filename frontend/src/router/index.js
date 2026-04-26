import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import ProductsView from '../views/inventory/ProductsView.vue'
import BatchesView from '../views/inventory/BatchesView.vue'
import LocationsView from '../views/inventory/LocationsView.vue'
import RequisitionsView from '../views/requisitions/RequisitionsView.vue'
import CreateRequisitionView from '../views/requisitions/CreateRequisitionView.vue'
import AlertsView from '../views/alerts/AlertsView.vue'
import TemperatureView from '../views/temperature/TemperatureView.vue'
import AuditLogsView from '../views/audit/AuditLogsView.vue'
import UsersView from '../views/users/UsersView.vue'

export const ROLES = {
  ADMIN: 'ADMIN',
  PHARMACIST: 'PHARMACIST',
  NURSE: 'NURSE',
  LOGISTICS: 'LOGISTICS',
  QUALITY_MANAGER: 'QUALITY_MANAGER',
}

const allRoles = Object.values(ROLES)

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'login', component: LoginView, meta: { guest: true } },
  { path: '/register', name: 'register', component: RegisterView, meta: { guest: true } },
  {
    path: '/',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'dashboard', component: DashboardView, meta: { roles: allRoles } },
      { path: 'inventory/products', name: 'products', component: ProductsView, meta: { roles: [ROLES.ADMIN, ROLES.PHARMACIST, ROLES.LOGISTICS] } },
      { path: 'inventory/batches', name: 'batches', component: BatchesView, meta: { roles: [ROLES.ADMIN, ROLES.PHARMACIST, ROLES.LOGISTICS] } },
      { path: 'inventory/locations', name: 'locations', component: LocationsView, meta: { roles: [ROLES.ADMIN, ROLES.LOGISTICS] } },
      { path: 'requisitions', name: 'requisitions', component: RequisitionsView, meta: { roles: [ROLES.ADMIN, ROLES.PHARMACIST, ROLES.NURSE, ROLES.LOGISTICS] } },
      { path: 'requisitions/create', name: 'create-requisition', component: CreateRequisitionView, meta: { roles: [ROLES.NURSE] } },
      { path: 'alerts', name: 'alerts', component: AlertsView, meta: { roles: [ROLES.ADMIN, ROLES.PHARMACIST, ROLES.QUALITY_MANAGER] } },
      { path: 'temperature', name: 'temperature', component: TemperatureView, meta: { roles: [ROLES.ADMIN, ROLES.QUALITY_MANAGER] } },
      { path: 'audit', name: 'audit', component: AuditLogsView, meta: { roles: [ROLES.ADMIN, ROLES.PHARMACIST, ROLES.QUALITY_MANAGER] } },
      { path: 'users', name: 'users', component: UsersView, meta: { roles: [ROLES.ADMIN] } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.guest && auth.isAuthenticated) {
    return { name: 'dashboard' }
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login' }
  }

  const roles = to.meta.roles
  if (roles && !roles.includes(auth.role)) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
