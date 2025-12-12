<template>
  <div class="min-h-screen bg-gray-100">
    <header>
      <Navbar />
    </header>

    <main class="p-6 max-w-4xl mx-auto">
      <Suspense>
        <template #default>
          <RouterView />
        </template>

        <template #fallback>
          <div class="text-center text-slate-500 py-10">
            Carregando...
          </div>
        </template>
      </Suspense>
    </main>
  </div>
</template>

<script setup lang="ts">
import Navbar from "./components/Navbar.vue";
</script>
