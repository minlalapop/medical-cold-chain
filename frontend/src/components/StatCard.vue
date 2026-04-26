<script setup>
defineProps({
  title: String,
  value: [String, Number],
  hint: String,
})
</script>

<template>
  <article class="stat-card">
    <span>{{ title }}</span>
    <strong>{{ value }}</strong>
    <small>{{ hint }}</small>
  </article>
</template>
