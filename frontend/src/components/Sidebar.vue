<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const auth = useAuthStore()

const items = [
  { label: 'Dashboard', to: '/dashboard', icon: 'D', roles: ['ADMIN', 'PHARMACIST', 'NURSE', 'LOGISTICS', 'QUALITY_MANAGER'] },
  { label: 'Products', to: '/inventory/products', icon: 'P', roles: ['ADMIN', 'PHARMACIST', 'LOGISTICS'] },
  { label: 'Batches', to: '/inventory/batches', icon: 'B', roles: ['ADMIN', 'PHARMACIST', 'LOGISTICS'] },
  { label: 'Locations', to: '/inventory/locations', icon: 'L', roles: ['ADMIN', 'LOGISTICS'] },
  { label: 'Requisitions', to: '/requisitions', icon: 'R', roles: ['ADMIN', 'PHARMACIST', 'NURSE', 'LOGISTICS'] },
  { label: 'New Request', to: '/requisitions/create', icon: 'N', roles: ['NURSE'] },
  { label: 'Alerts', to: '/alerts', icon: 'A', roles: ['ADMIN', 'PHARMACIST', 'QUALITY_MANAGER'] },
  { label: 'Temperature', to: '/temperature', icon: 'T', roles: ['ADMIN', 'QUALITY_MANAGER'] },
  { label: 'Audit', to: '/audit', icon: 'J', roles: ['ADMIN', 'PHARMACIST', 'QUALITY_MANAGER'] },
  { label: 'Users', to: '/users', icon: 'U', roles: ['ADMIN'] },
]

const visibleItems = computed(() => items.filter((item) => item.roles.includes(auth.role)))
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">M</div>
      <div>
        <h1>MedCold</h1>
        <p>Chain Control</p>
      </div>
    </div>

    <nav class="nav-list">
      <RouterLink v-for="item in visibleItems" :key="item.to" :to="item.to" class="nav-item">
        <span class="nav-icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>
  </aside>
</template>
