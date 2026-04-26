<script setup>
import { onMounted, reactive, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { inventoryService } from '../../services/inventoryService'

const locations = ref([])
const modalOpen = ref(false)
const loading = ref(false)
const form = reactive({ name: '', type: 'FRIDGE' })

async function load() {
  loading.value = true
  try {
    locations.value = (await inventoryService.getLocations()).data
  } finally {
    loading.value = false
  }
}

async function save() {
  await inventoryService.createLocation(form)
  modalOpen.value = false
  Object.assign(form, { name: '', type: 'FRIDGE' })
  load()
}

async function remove(id) {
  await inventoryService.deleteLocation(id)
  load()
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head">
      <h3>Locations</h3>
      <button class="btn btn-primary" @click="modalOpen = true">Add location</button>
    </div>
    <DataTable :loading="loading" :rows="locations" :columns="[
      { key: 'name', label: 'Name' },
      { key: 'type', label: 'Type' },
    ]">
      <template #actions="{ row }">
        <button class="btn btn-danger" @click="remove(row.id)">Delete</button>
      </template>
    </DataTable>
    <Modal :open="modalOpen" title="Add location" @close="modalOpen = false">
      <form class="form-grid" @submit.prevent="save">
        <label>Name<input v-model="form.name" required /></label>
        <label>Type<select v-model="form.type"><option>PHARMACY</option><option>FRIDGE</option><option>WAREHOUSE</option><option>SERVICE_ROOM</option></select></label>
        <button class="btn btn-primary span-2">Save</button>
      </form>
    </Modal>
  </div>
</template>
