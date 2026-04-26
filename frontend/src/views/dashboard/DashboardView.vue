<script setup>
import { computed, onMounted, ref } from 'vue'
import StatCard from '../../components/StatCard.vue'
import DataTable from '../../components/DataTable.vue'
import { inventoryService } from '../../services/inventoryService'
import { requisitionService } from '../../services/requisitionService'
import { alertService } from '../../services/alertService'
import { auditService } from '../../services/auditService'
import { useAuthStore } from '../../stores/authStore'

const auth = useAuthStore()
const stats = ref({ products: 0, alerts: 0, requisitions: 0 })
const alerts = ref([])
const logs = ref([])
const requisitions = ref([])
const departmentStats = ref({})
const lowStock = ref([])
const loading = ref(false)

const pendingRequisitions = computed(() => requisitions.value.filter((item) => item.status === 'PENDING').length)
const deliveredRequisitions = computed(() => requisitions.value.filter((item) => item.status === 'DELIVERED').length)
const criticalAlerts = computed(() => alerts.value.filter((item) => item.priority === 'CRITICAL').length)
const myRequisitions = computed(() => requisitions.value.filter((item) => item.requesterName === auth.displayName).length)
const roleCards = computed(() => {
  const cards = {
    ADMIN: [
      ['System users', stats.value.requisitions, 'Total requisition history'],
      ['Open alerts', stats.value.alerts, 'System-wide issues'],
      ['Audit events', logs.value.length, 'Recent tracked actions'],
    ],
    PHARMACIST: [
      ['Pending approvals', pendingRequisitions.value, 'Requests waiting'],
      ['Delivered requests', deliveredRequisitions.value, 'Completed flow'],
      ['Low stock', lowStock.value.length, 'Inventory pressure'],
    ],
    NURSE: [
      ['My requests', myRequisitions.value, 'Created with this account'],
      ['Pending requests', pendingRequisitions.value, 'Waiting pharmacy'],
      ['Delivered', deliveredRequisitions.value, 'Ready history'],
    ],
    LOGISTICS: [
      ['Products', stats.value.products, 'Inventory catalog'],
      ['Low stock', lowStock.value.length, 'Needs restock'],
      ['Open alerts', stats.value.alerts, 'Operational issues'],
    ],
    QUALITY_MANAGER: [
      ['Critical alerts', criticalAlerts.value, 'Highest priority'],
      ['Open alerts', stats.value.alerts, 'Quality watch'],
      ['Audit events', logs.value.length, 'Recent traceability'],
    ],
  }

  return cards[auth.role] || cards.ADMIN
})
const departmentChart = computed(() => {
  const entries = Object.entries(departmentStats.value)
  const max = Math.max(...entries.map(([, count]) => count), 1)

  return entries.map(([department, count]) => ({
    department: department.replaceAll('_', ' '),
    count,
    width: `${(count / max) * 100}%`,
  }))
})

async function load() {
  loading.value = true
  try {
    await alertService.checkAll().catch(() => null)

    const [productsRes, alertsRes, reqRes, logsRes, deptStatsRes, lowStockRes] = await Promise.allSettled([
      inventoryService.getProducts(),
      alertService.getOpen(),
      requisitionService.getAll(),
      auditService.getAll(),
      requisitionService.getDepartmentStats(),
      inventoryService.getLowStock(),
    ])
    stats.value.products = productsRes.value?.data?.length || 0
    stats.value.alerts = alertsRes.value?.data?.length || 0
    stats.value.requisitions = reqRes.value?.data?.length || 0
    alerts.value = (alertsRes.value?.data || []).slice(0, 5)
    requisitions.value = reqRes.value?.data || []
    logs.value = (logsRes.value?.data || []).slice(-5).reverse()
    departmentStats.value = deptStatsRes.value?.data || {}
    lowStock.value = lowStockRes.value?.data || []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-stack">
    <div class="stats-grid">
      <StatCard title="Products" :value="stats.products" hint="Registered inventory items" />
      <StatCard title="Open alerts" :value="stats.alerts" hint="Needs attention" />
      <StatCard title="Requisitions" :value="stats.requisitions" hint="Total requests" />
    </div>

    <section class="content-grid">
      <div class="card">
        <div class="section-head">
          <h3>{{ auth.role }} snapshot</h3>
        </div>
        <div class="mini-stats">
          <div v-for="card in roleCards" :key="card[0]">
            <span>{{ card[0] }}</span>
            <strong>{{ card[1] }}</strong>
            <small>{{ card[2] }}</small>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="section-head"><h3>Requests by department</h3></div>
        <div class="chart-list">
          <div v-for="item in departmentChart" :key="item.department" class="chart-row">
            <span>{{ item.department }}</span>
            <div class="chart-track"><div class="chart-bar" :style="{ width: item.width }" /></div>
            <strong>{{ item.count }}</strong>
          </div>
          <p v-if="departmentChart.length === 0" class="muted-text">No requisitions yet</p>
        </div>
      </div>
    </section>

    <section class="content-grid">
      <div class="card">
        <div class="section-head"><h3>Recent alerts</h3></div>
        <DataTable :loading="loading" :rows="alerts" :columns="[
          { key: 'priority', label: 'Priority' },
          { key: 'type', label: 'Type' },
          { key: 'title', label: 'Title' },
        ]" />
      </div>
      <div class="card">
        <div class="section-head"><h3>Recent activity</h3></div>
        <DataTable :loading="loading" :rows="logs" :columns="[
          { key: 'actor', label: 'Actor' },
          { key: 'action', label: 'Action' },
          { key: 'serviceName', label: 'Service' },
        ]" />
      </div>
    </section>
  </div>
</template>
