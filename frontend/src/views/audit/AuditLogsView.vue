<script setup>
import { onMounted, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import { auditService } from '../../services/auditService'

const logs = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    logs.value = (await auditService.getAll()).data
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head"><h3>Audit logs</h3></div>
    <DataTable :loading="loading" :rows="logs" :columns="[
      { key: 'timestamp', label: 'Time' },
      { key: 'actor', label: 'Actor' },
      { key: 'actorRole', label: 'Role' },
      { key: 'serviceName', label: 'Service' },
      { key: 'action', label: 'Action' },
      { key: 'severity', label: 'Severity' },
      { key: 'details', label: 'Details' },
    ]" />
  </div>
</template>
