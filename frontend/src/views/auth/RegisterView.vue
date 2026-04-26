<script setup>
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({
  fullName: '',
  email: '',
  password: '',
  role: 'NURSE',
})

async function submit() {
  await auth.register(form)
  router.push('/dashboard')
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card wide">
      <div class="auth-heading">
        <div class="brand-mark large">M</div>
        <h1>Create account</h1>
        <p>Register an internal hospital user.</p>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label>Full name<input v-model="form.fullName" required /></label>
        <label>Email<input v-model="form.email" type="email" required /></label>
        <label>Password<input v-model="form.password" type="password" required /></label>
        <label>
          Role
          <select v-model="form.role">
            <option>PHARMACIST</option>
            <option>NURSE</option>
            <option>LOGISTICS</option>
            <option>QUALITY_MANAGER</option>
          </select>
        </label>
        <p v-if="auth.error" class="error-text span-2">{{ auth.error }}</p>
        <button class="btn btn-primary span-2" :disabled="auth.loading">Register</button>
      </form>
      <p class="auth-link">Already registered? <RouterLink to="/login">Login</RouterLink></p>
    </section>
  </main>
</template>
