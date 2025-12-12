<template>
  <teleport to="body">
    <div
      v-if="open"
      class="fixed inset-0 z-50 grid place-items-center p-4"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="titleId"
      @keydown.esc="$emit('close')"
    >
      <div class="absolute inset-0 bg-black/40" @click="$emit('close')"></div>

      <div class="relative w-full max-w-lg rounded-xl bg-white shadow-xl border border-slate-200">
        <header class="flex items-center justify-between p-4 border-b">
          <h2 :id="titleId" class="text-lg font-semibold">
            <slot name="title">Título</slot>
          </h2>
          <button class="p-1 rounded hover:bg-slate-100" @click="$emit('close')" aria-label="Fechar">✕</button>
        </header>

        <section class="p-4">
          <slot />
        </section>

        <footer class="flex justify-end gap-2 p-4 border-t">
          <slot name="footer" />
        </footer>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { computed } from "vue";
const props = defineProps<{ open: boolean; titleId?: string }>();
defineEmits<{ (e: "close"): void }>();
const titleId = computed(() => props.titleId ?? "modal-title");
</script>
