<script setup>
import Sidebar from '../components/Sidebar.vue'
import Topbar from '../components/Topbar.vue'
</script>

<template>
  <div class="app-shell">
    <Sidebar />
    <main class="main-panel">
      <Topbar />
      <section class="page-content">
        <RouterView />
      </section>
    </main>
  </div>
</template>
