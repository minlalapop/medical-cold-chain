<script setup>
import { onMounted, reactive, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { inventoryService } from '../../services/inventoryService'

const products = ref([])
const loading = ref(false)
const modalOpen = ref(false)
const editingId = ref(null)
const form = reactive({ name: '', description: '', category: 'VACCINE', requiresColdChain: true, minStockThreshold: 0 })

function reset() {
  editingId.value = null
  Object.assign(form, { name: '', description: '', category: 'VACCINE', requiresColdChain: true, minStockThreshold: 0 })
}

async function load() {
  loading.value = true
  try {
    products.value = (await inventoryService.getProducts()).data
  } finally {
    loading.value = false
  }
}

function edit(row) {
  editingId.value = row.id
  Object.assign(form, row)
  modalOpen.value = true
}

async function save() {
  if (editingId.value) await inventoryService.updateProduct(editingId.value, form)
  else await inventoryService.createProduct(form)
  modalOpen.value = false
  reset()
  load()
}

async function remove(id) {
  await inventoryService.deleteProduct(id)
  load()
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head">
      <h3>Products</h3>
      <button class="btn btn-primary" @click="reset(); modalOpen = true">Add product</button>
    </div>
    <DataTable :loading="loading" :rows="products" :columns="[
      { key: 'name', label: 'Name' },
      { key: 'category', label: 'Category' },
      { key: 'requiresColdChain', label: 'Cold chain', format: v => v ? 'Yes' : 'No' },
      { key: 'minStockThreshold', label: 'Min stock' },
    ]">
      <template #actions="{ row }">
        <button class="btn btn-soft" @click="edit(row)">Edit</button>
        <button class="btn btn-danger" @click="remove(row.id)">Delete</button>
      </template>
    </DataTable>
    <Modal :open="modalOpen" :title="editingId ? 'Edit product' : 'Add product'" @close="modalOpen = false">
      <form class="form-grid" @submit.prevent="save">
        <label>Name<input v-model="form.name" required /></label>
        <label>Description<input v-model="form.description" /></label>
        <label>Category<select v-model="form.category"><option>MEDICINE</option><option>VACCINE</option><option>MEDICAL_DEVICE</option></select></label>
        <label>Min stock<input v-model.number="form.minStockThreshold" type="number" min="0" /></label>
        <label class="checkbox-row"><input v-model="form.requiresColdChain" type="checkbox" /> Requires cold chain</label>
        <button class="btn btn-primary span-2">Save</button>
      </form>
    </Modal>
  </div>
</template>
