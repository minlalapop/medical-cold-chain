<script setup>
import { onMounted, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import { userService } from '../../services/userService'

const users = ref([])
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  try {
    users.value = (await userService.getAll()).data
  } finally {
    loading.value = false
  }
}

async function remove(id) {
  error.value = ''
  try {
    await userService.delete(id)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to delete user'
  }
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head">
      <h3>Users</h3>
    </div>
    <p v-if="error" class="error-text">{{ error }}</p>
    <DataTable :loading="loading" :rows="users" :columns="[
      { key: 'fullName', label: 'Name' },
      { key: 'email', label: 'Email' },
      { key: 'department', label: 'Department' },
      { key: 'role', label: 'Role' },
      { key: 'status', label: 'Status' },
    ]">
      <template #actions="{ row }">
        <button v-if="row.role !== 'ADMIN'" class="btn btn-danger" @click="remove(row.id)">Delete</button>
        <span v-else class="muted-text">Protected</span>
      </template>
    </DataTable>
  </div>
</template>
