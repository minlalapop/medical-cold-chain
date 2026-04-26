<script setup>
defineProps({
  title: String,
  open: Boolean,
})

defineEmits(['close'])
</script>

<template>
  <div v-if="open" class="modal-backdrop" @click.self="$emit('close')">
    <section class="modal-card">
      <header class="modal-header">
        <h3>{{ title }}</h3>
        <button class="icon-btn" @click="$emit('close')">x</button>
      </header>
      <slot />
    </section>
  </div>
</template>
