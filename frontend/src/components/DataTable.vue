<script setup>
defineProps({
  columns: {
    type: Array,
    default: () => [],
  },
  rows: {
    type: Array,
    default: () => [],
  },
  loading: Boolean,
  emptyText: {
    type: String,
    default: 'No data found',
  },
})
</script>

<template>
  <div class="table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
          <th v-if="$slots.actions">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td :colspan="columns.length + ($slots.actions ? 1 : 0)">Loading...</td>
        </tr>
        <tr v-else-if="rows.length === 0">
          <td :colspan="columns.length + ($slots.actions ? 1 : 0)">{{ emptyText }}</td>
        </tr>
        <tr v-for="row in rows" v-else :key="row.id || JSON.stringify(row)">
          <td v-for="column in columns" :key="column.key">
            <slot :name="column.key" :row="row">
              {{ column.format ? column.format(row[column.key], row) : row[column.key] }}
            </slot>
          </td>
          <td v-if="$slots.actions">
            <div class="table-actions">
              <slot name="actions" :row="row" />
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
