<template>
  <div
    class="bg-white shadow-md rounded-lg p-4 flex items-start justify-between gap-4"
  >
    <!-- Conteúdo -->
    <div class="flex-1">
      <div class="flex items-center justify-between gap-4">
        <h3 class="text-lg font-semibold text-slate-800">
          {{ task.title }}
        </h3>

        <span
          :class="badgeClass"
          class="text-xs font-medium px-2 py-1 rounded"
        >
          {{ task.statusDescription ?? task.status }}
        </span>
      </div>

      <p
        v-if="task.description?.trim()"
        class="mt-2 text-sm text-slate-600"
      >
        {{ task.description }}
      </p>

      <p
        v-else
        class="mt-2 text-sm text-slate-400 italic"
      >
        Sem descrição
      </p>

      <div class="mt-3 text-xs text-slate-500">
        <span>Criado: {{ formatDate(task.createdAt) }}</span>
        <span class="mx-2">•</span>
        <span>Atualizado: {{ formatDate(task.updatedAt) }}</span>
      </div>
    </div>

    <!-- Ações -->
    <div class="flex flex-col items-end gap-2">
      <button
        type="button"
        class="px-3 py-1 rounded-md border border-slate-200 text-sm hover:bg-slate-50"
        aria-label="Editar tarefa"
        @click="emitEdit"
      >
        Editar
      </button>

      <button
        type="button"
        class="px-3 py-1 rounded-md bg-red-600 text-white text-sm hover:bg-red-700"
        aria-label="Excluir tarefa"
        @click="onDelete"
      >
        Excluir
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits } from 'vue';

export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export interface TaskResponse {
  id: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  statusDescription?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

const props = defineProps<{
  task: TaskResponse;
}>();

const emit = defineEmits<{
  (e: 'edit', task: TaskResponse): void;
  (e: 'delete', id: string): void;
}>();

function emitEdit() {
  emit('edit', props.task);
}

function onDelete() {
    emit('delete', props.task.id);
}

function formatDate(dt?: string) {
  if (!dt) return '-';

  const date = new Date(dt);
  if (Number.isNaN(date.getTime())) return dt;

  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(date);
}

const badgeClass = computed(() => {
  switch (props.task.status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800';
    case 'IN_PROGRESS':
      return 'bg-blue-100 text-blue-800';
    case 'COMPLETED':
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-slate-100 text-slate-800';
  }
});
</script>

<style scoped>

</style>
