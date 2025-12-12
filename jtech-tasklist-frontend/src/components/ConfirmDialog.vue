<template>
  <BaseModal :open="open" @close="onCancel">
    <template #title>{{ title }}</template>

    <p class="text-slate-600 whitespace-pre-line">{{ message }}</p>

    <template #footer>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md border border-slate-200 hover:bg-slate-50"
        :disabled="loading"
        @click="onCancel"
      >
        {{ cancelLabel }}
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md bg-red-600 text-white hover:bg-red-700 disabled:opacity-60"
        :disabled="loading"
        @click="$emit('confirm')"
      >
        {{ confirmLabel }}
      </button>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import BaseModal from "@/components/BaseModal.vue";

withDefaults(defineProps<{
  open: boolean;
  title?: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  loading?: boolean;
}>(), {
  title: "Confirmar ação",
  confirmLabel: "Confirmar",
  cancelLabel: "Cancelar",
  loading: false,
});

const emit = defineEmits<{ (e: "confirm"): void; (e: "cancel"): void }>();
function onCancel() { emit("cancel"); }
</script>
