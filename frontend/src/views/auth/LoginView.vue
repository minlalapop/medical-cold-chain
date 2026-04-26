<script setup>
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ email: '', password: '' })

async function submit() {
  await auth.login(form)
  router.push('/dashboard')
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <div class="auth-heading">
        <div class="brand-mark large">M</div>
        <h1>Welcome back</h1>
        <p>Sign in to manage medical stock and cold chain operations.</p>
      </div>
      <form class="form-stack" @submit.prevent="submit">
        <label>Email<input v-model="form.email" type="email" required /></label>
        <label>Password<input v-model="form.password" type="password" required /></label>
        <p v-if="auth.error" class="error-text">{{ auth.error }}</p>
        <button class="btn btn-primary" :disabled="auth.loading">Login</button>
      </form>
      <p class="auth-link">No account? <RouterLink to="/register">Create one</RouterLink></p>
    </section>
  </main>
</template>
