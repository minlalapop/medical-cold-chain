<script setup>
import { computed, onMounted, ref } from 'vue'
import DataTable from '../../components/DataTable.vue'
import Modal from '../../components/Modal.vue'
import { requisitionService } from '../../services/requisitionService'
import { useAuthStore } from '../../stores/authStore'

const auth = useAuthStore()
const requisitions = ref([])
const loading = ref(false)
const error = ref('')
const detailsOpen = ref(false)
const selectedRequest = ref(null)

const statusRank = {
  PENDING: 1,
  APPROVED: 2,
  DELIVERED: 3,
  REJECTED: 4,
  CANCELLED: 5,
}

const sortedRequisitions = computed(() => [...requisitions.value].sort((a, b) => {
  const rankDiff = (statusRank[a.status] || 99) - (statusRank[b.status] || 99)

  if (rankDiff !== 0) {
    return rankDiff
  }

  return new Date(b.requestedAt || 0) - new Date(a.requestedAt || 0)
}))

function formatDate(value) {
  if (!value) {
    return '-'
  }

  return new Date(value).toLocaleString()
}

function formatShortDate(value) {
  if (!value) {
    return '-'
  }

  return new Date(value).toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
  })
}

function itemsSummary(items = []) {
  const products = items.length
  const units = items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)

  return `${products} ${products === 1 ? 'product' : 'products'}, ${units} ${units === 1 ? 'unit' : 'units'}`
}

function openDetails(row) {
  selectedRequest.value = row
  detailsOpen.value = true
}

async function load() {
  loading.value = true
  try {
    requisitions.value = (await requisitionService.getAll()).data
  } finally {
    loading.value = false
  }
}

async function approve(id) {
  error.value = ''
  try {
    await requisitionService.approve(id, auth.displayName)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to approve requisition'
  }
}

async function reject(id) {
  const reason = window.prompt('Reason') || 'No reason provided'
  error.value = ''
  try {
    await requisitionService.reject(id, auth.displayName, reason)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to reject requisition'
  }
}

async function deliver(id) {
  error.value = ''
  try {
    await requisitionService.deliver(id, auth.displayName)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to deliver requisition'
  }
}

async function cancel(id) {
  const reason = window.prompt('Cancellation reason') || 'Cancelled from dashboard'
  error.value = ''
  try {
    await requisitionService.cancel(id, auth.displayName, reason)
    load()
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to cancel requisition'
  }
}

onMounted(load)
</script>

<template>
  <div class="card">
    <div class="section-head"><h3>Requisitions</h3></div>
    <p v-if="error" class="error-text">{{ error }}</p>
    <DataTable :loading="loading" :rows="sortedRequisitions" :columns="[
      { key: 'id', label: 'ID', format: v => `#${v}` },
      { key: 'department', label: 'Department', format: v => String(v || '-').replaceAll('_', ' ') },
      { key: 'requesterName', label: 'Requester' },
      { key: 'items', label: 'Items summary', format: itemsSummary },
      { key: 'status', label: 'Status' },
      { key: 'requestedAt', label: 'Requested date', format: formatShortDate },
    ]">
      <template #actions="{ row }">
        <button class="btn btn-ghost" @click="openDetails(row)">View</button>
        <button v-if="auth.role === 'PHARMACIST' && row.status === 'PENDING'" class="btn btn-soft" @click="approve(row.id)">Approve</button>
        <button v-if="auth.role === 'PHARMACIST' && row.status === 'PENDING'" class="btn btn-danger" @click="reject(row.id)">Reject</button>
        <button v-if="auth.role === 'LOGISTICS' && row.status === 'APPROVED'" class="btn btn-primary" @click="deliver(row.id)">Deliver</button>
        <button v-if="row.status === 'PENDING' && (auth.role === 'ADMIN' || row.requesterName === auth.displayName)" class="btn btn-ghost" @click="cancel(row.id)">Cancel</button>
      </template>
    </DataTable>

    <Modal :open="detailsOpen" :title="selectedRequest ? `Request #${selectedRequest.id}` : 'Request details'" @close="detailsOpen = false">
      <div v-if="selectedRequest" class="details-grid">
        <section>
          <h4>Summary</h4>
          <p><strong>Requester:</strong> {{ selectedRequest.requesterName }}</p>
          <p><strong>Department:</strong> {{ selectedRequest.department?.replaceAll('_', ' ') }}</p>
          <p><strong>Status:</strong> {{ selectedRequest.status }}</p>
          <p><strong>Requested at:</strong> {{ formatDate(selectedRequest.requestedAt) }}</p>
        </section>

        <section>
          <h4>Items</h4>
          <ul class="details-list">
            <li v-for="item in selectedRequest.items" :key="item.id">
              {{ item.productName || `Product ${item.productId}` }}, qty {{ item.quantity }}
            </li>
          </ul>
        </section>

        <section>
          <h4>Validation</h4>
          <p><strong>Validated by:</strong> {{ selectedRequest.validatedBy || '-' }}</p>
          <p><strong>Validated at:</strong> {{ formatDate(selectedRequest.validatedAt) }}</p>
        </section>

        <section>
          <h4>Delivery</h4>
          <p><strong>Delivered at:</strong> {{ formatDate(selectedRequest.deliveredAt) }}</p>
        </section>

        <section>
          <h4>Rejection / cancellation reason</h4>
          <p>{{ selectedRequest.rejectionReason || '-' }}</p>
        </section>
      </div>
    </Modal>
  </div>
</template>
