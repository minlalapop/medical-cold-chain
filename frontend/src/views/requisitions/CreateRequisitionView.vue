<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { inventoryService } from '../../services/inventoryService'
import { requisitionService } from '../../services/requisitionService'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const auth = useAuthStore()
const products = ref([])
const departments = ref([])
const error = ref('')
const form = reactive({
  requesterName: auth.displayName,
  department: '',
  items: [{ productId: null, productName: '', quantity: 1 }],
})

onMounted(async () => {
  const [productsRes, departmentsRes] = await Promise.all([
    inventoryService.getProducts(),
    requisitionService.getDepartments(),
  ])
  products.value = productsRes.data
  departments.value = departmentsRes.data
})

function addItem() {
  form.items.push({ productId: null, productName: '', quantity: 1 })
}

function syncProduct(item) {
  const product = products.value.find((p) => p.id === item.productId)
  item.productName = product?.name || ''
}

function removeItem(index) {
  form.items.splice(index, 1)
  if (form.items.length === 0) {
    addItem()
  }
}

async function submit() {
  error.value = ''
  try {
    await requisitionService.create(form)
    router.push('/requisitions')
  } catch (err) {
    error.value = err.response?.data?.error || 'Unable to create requisition'
  }
}
</script>

<template>
  <div class="card narrow">
    <div class="section-head"><h3>Create requisition</h3></div>
    <p v-if="error" class="error-text">{{ error }}</p>
    <form class="form-stack" @submit.prevent="submit">
      <label>Requester<input v-model="form.requesterName" required /></label>
      <label>
        Department
        <select v-model="form.department" required>
          <option disabled value="">Select department</option>
          <option v-for="department in departments" :key="department" :value="department">
            {{ department.replaceAll('_', ' ') }}
          </option>
        </select>
      </label>
      <div v-for="(item, index) in form.items" :key="index" class="item-row">
        <label>
          Product
          <select v-model.number="item.productId" required @change="syncProduct(item)">
            <option disabled :value="null">Select product</option>
            <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option>
          </select>
        </label>
        <label>Quantity<input v-model.number="item.quantity" type="number" min="1" required /></label>
        <button type="button" class="icon-btn" title="Remove item" @click="removeItem(index)">x</button>
      </div>
      <button type="button" class="btn btn-soft" @click="addItem">Add item</button>
      <button class="btn btn-primary">Create request</button>
    </form>
  </div>
</template>
