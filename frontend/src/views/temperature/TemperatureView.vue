<script setup>
import { onMounted, reactive, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { inventoryService } from '../../services/inventoryService'
import { temperatureService } from '../../services/temperatureService'
import { useAuthStore } from '../../stores/authStore'

const auth = useAuthStore()
const readings = ref([])
const incidents = ref([])
const locations = ref([])
const modalOpen = ref(false)
const loading = ref(false)
const form = reactive({ locationId: 1, locationName: '', temperature: 5, expectedMin: 2, expectedMax: 8, source: 'MANUAL' })

async function load() {
  loading.value = true
  try {
    const [r, i, l] = await Promise.all([
      temperatureService.getReadings(),
      temperatureService.getOpenIncidents(),
      inventoryService.getLocations(),
    ])
    readings.value = r.data
    incidents.value = i.data
    locations.value = l.data
  } finally {
    loading.value = false
  }
}

function selectLocation() {
  const location = locations.value.find((item) => item.id === form.locationId)
  form.locationName = location?.name || ''
}

async function save() {
  await temperatureService.createReading(form)
  modalOpen.value = false
  load()
}

async function resolve(id) {
  await temperatureService.resolveIncident(id, { resolvedBy: auth.displayName, resolutionNote: 'Resolved from dashboard' })
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-stack">
    <div class="card">
      <div class="section-head">
        <h3>Temperature readings</h3>
        <button class="btn btn-primary" @click="modalOpen = true">Add reading</button>
      </div>
      <DataTable :loading="loading" :rows="readings" :columns="[
        { key: 'locationName', label: 'Location' },
        { key: 'temperature', label: 'Temp' },
        { key: 'source', label: 'Source' },
        { key: 'recordedAt', label: 'Recorded' },
      ]" />
    </div>
    <div class="card">
      <div class="section-head"><h3>Open incidents</h3></div>
      <DataTable :loading="loading" :rows="incidents" :columns="[
        { key: 'locationName', label: 'Location' },
        { key: 'severity', label: 'Severity' },
        { key: 'temperature', label: 'Temp' },
        { key: 'message', label: 'Message' },
      ]">
        <template #actions="{ row }">
          <button class="btn btn-soft" @click="resolve(row.id)">Resolve</button>
        </template>
      </DataTable>
    </div>
    <Modal :open="modalOpen" title="Add reading" @close="modalOpen = false">
      <form class="form-grid" @submit.prevent="save">
        <label>
          Location
          <select v-model.number="form.locationId" required @change="selectLocation">
            <option v-for="location in locations" :key="location.id" :value="location.id">
              {{ location.name }} - {{ location.type }}
            </option>
          </select>
        </label>
        <label>Location name<input v-model="form.locationName" readonly required /></label>
        <label>Temperature<input v-model.number="form.temperature" type="number" step="0.1" /></label>
        <label>Source<select v-model="form.source"><option>MANUAL</option><option>SENSOR</option></select></label>
        <label>Expected min<input v-model.number="form.expectedMin" type="number" step="0.1" /></label>
        <label>Expected max<input v-model.number="form.expectedMax" type="number" step="0.1" /></label>
        <button class="btn btn-primary span-2">Save</button>
      </form>
    </Modal>
  </div>
</template>
