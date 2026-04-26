<script setup>
import { onMounted, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { alertService } from '../../services/alertService'
import { inventoryService } from '../../services/inventoryService'
import { temperatureService } from '../../services/temperatureService'
import { useAuthStore } from '../../stores/authStore'

const auth = useAuthStore()
const alerts = ref([])
const loading = ref(false)
const error = ref('')
const selectedAlert = ref(null)
const modalOpen = ref(false)
const resolutionNote = ref('')

async function load() {
  loading.value = true
  try {
    alerts.value = (await alertService.checkAll()).data
  } finally {
    loading.value = false
  }
}

function openResolve(alert) {
  selectedAlert.value = alert
  resolutionNote.value = ''
  modalOpen.value = true
}

async function resolveSelected() {
  error.value = ''
  const alert = selectedAlert.value

  if (!alert) {
    return
  }

  try {
    if ((alert.type === 'EXPIRY' || alert.type === 'CASCADE') && alert.batchId) {
      await inventoryService.deleteBatch(alert.batchId)
    }

    if (alert.type === 'TEMPERATURE' && alert.sourceIncidentId) {
      await temperatureService.resolveIncident(alert.sourceIncidentId, {
        resolvedBy: auth.displayName,
        resolutionNote: resolutionNote.value || 'Corrective action completed from alert dashboard',
      })
    }

    await alertService.resolve(alert.id, {
      resolvedBy: auth.displayName,
      resolutionNote: resolutionNote.value || 'Corrective action completed from alert dashboard',
    })

    modalOpen.value = false
    selectedAlert.value = null
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to resolve alert. Correct the source issue first.'
  }
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head">
      <h3>Alerts</h3>
      <button class="btn btn-soft" @click="load">Refresh</button>
    </div>
    <p v-if="error" class="error-text">{{ error }}</p>
    <DataTable :loading="loading" :rows="alerts" :columns="[
      { key: 'priority', label: 'Priority' },
      { key: 'type', label: 'Type' },
      { key: 'title', label: 'Title' },
      { key: 'locationName', label: 'Location' },
      { key: 'status', label: 'Status' },
    ]">
      <template #actions="{ row }">
        <button class="btn btn-soft" @click="openResolve(row)">Resolve</button>
      </template>
    </DataTable>
    <Modal :open="modalOpen" title="Resolve alert" @close="modalOpen = false">
      <div v-if="selectedAlert" class="form-stack">
        <p class="muted-text">
          {{ selectedAlert.type === 'TEMPERATURE'
            ? 'This will resolve the linked temperature incident, then close this alert.'
            : 'This will remove the linked batch from inventory, then close this alert.' }}
        </p>
        <p><strong>{{ selectedAlert.title }}</strong></p>
        <p>{{ selectedAlert.message }}</p>
        <label>
          Corrective action note
          <textarea v-model="resolutionNote" placeholder="Example: batch removed from fridge and quarantined." />
        </label>
        <button class="btn btn-primary" @click="resolveSelected">Confirm correction</button>
      </div>
    </Modal>
  </div>
</template>
