<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { inventoryService } from '../../services/inventoryService'

const batches = ref([])
const products = ref([])
const locations = ref([])
const loading = ref(false)
const modalOpen = ref(false)
const error = ref('')
const form = reactive({ batchName: '', quantity: 1, expiryDate: '', productId: null, locationId: null })
const filters = reactive({ productId: '', expiryBefore: '', expiryAfter: '' })

const filteredBatches = computed(() => batches.value.filter((batch) => {
  const matchesProduct = !filters.productId || batch.product?.id === Number(filters.productId)
  const matchesAfter = !filters.expiryAfter || batch.expiryDate >= filters.expiryAfter
  const matchesBefore = !filters.expiryBefore || batch.expiryDate <= filters.expiryBefore

  return matchesProduct && matchesAfter && matchesBefore
}))

async function load() {
  loading.value = true
  try {
    const [b, p, l] = await Promise.all([inventoryService.getBatches(), inventoryService.getProducts(), inventoryService.getLocations()])
    batches.value = b.data
    products.value = p.data
    locations.value = l.data
  } finally {
    loading.value = false
  }
}

async function save() {
  error.value = ''
  try {
    await inventoryService.createBatch(form)
    modalOpen.value = false
    Object.assign(form, { batchName: '', quantity: 1, expiryDate: '', productId: null, locationId: null })
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to save batch'
  }
}

async function removeBatch(id) {
  if (!window.confirm('Delete this batch from inventory?')) {
    return
  }

  error.value = ''
  try {
    await inventoryService.deleteBatch(id)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to delete batch'
  }
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head">
      <h3>Batches</h3>
      <button class="btn btn-primary" @click="modalOpen = true">Add batch</button>
    </div>
    <p v-if="error" class="error-text">{{ error }}</p>
    <div class="filters-grid">
      <label>
        Product
        <select v-model="filters.productId">
          <option value="">All products</option>
          <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option>
        </select>
      </label>
      <label>Expiry after<input v-model="filters.expiryAfter" type="date" /></label>
      <label>Expiry before<input v-model="filters.expiryBefore" type="date" /></label>
    </div>
    <DataTable :loading="loading" :rows="filteredBatches" :columns="[
      { key: 'batchNumber', label: 'Batch' },
      { key: 'batchName', label: 'Name', format: v => v || '-' },
      { key: 'quantity', label: 'Qty' },
      { key: 'expiryDate', label: 'Expiry' },
      { key: 'product', label: 'Product', format: v => v?.name || '-' },
      { key: 'location', label: 'Location', format: v => v?.name || '-' },
    ]">
      <template #actions="{ row }">
        <button class="btn btn-danger" @click="removeBatch(row.id)">Delete</button>
      </template>
    </DataTable>
    <Modal :open="modalOpen" title="Add batch" @close="modalOpen = false">
      <form class="form-grid" @submit.prevent="save">
        <label>Batch name<input v-model="form.batchName" required placeholder="ex: Donation urgente" /></label>
        <label>Quantity<input v-model.number="form.quantity" type="number" min="1" /></label>
        <label>Expiry date<input v-model="form.expiryDate" type="date" required /></label>
        <label>Product<select v-model.number="form.productId" required><option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option></select></label>
        <label>Location<select v-model.number="form.locationId" required><option v-for="l in locations" :key="l.id" :value="l.id">{{ l.name }}</option></select></label>
        <button class="btn btn-primary span-2">Save</button>
      </form>
    </Modal>
  </div>
</template>
