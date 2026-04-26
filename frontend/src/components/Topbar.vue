<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">Medical cold chain</p>
      <h2>Operations dashboard</h2>
    </div>
    <div class="user-box">
      <div class="user-meta">
        <strong>{{ auth.displayName }}</strong>
        <span>{{ auth.role }}</span>
      </div>
      <button class="btn btn-ghost" @click="logout">Logout</button>
    </div>
  </header>
</template>
