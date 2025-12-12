<template>
  <div class="max-w-5xl mx-auto mt-8">
    <!-- Cabeçalho -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-slate-800">Minhas Tarefas</h1>

      <button
        type="button"
        @click="goToCreate"
        class="px-4 py-2 rounded-lg bg-indigo-600 text-white shadow hover:bg-indigo-700 transition"
      >
        + Nova Tarefa
      </button>
    </div>

    <!-- Barra de status e ações -->
    <div class="flex items-center justify-between mb-4">
      <p class="text-slate-600 text-sm">
        Total: <strong>{{ tasks.length }}</strong> tarefas
      </p>

      <button
        type="button"
        @click="reload"
        :disabled="loading"
        class="text-sm px-3 py-1 rounded-md border border-slate-200 hover:bg-slate-50 transition disabled:opacity-60 disabled:cursor-not-allowed"
      >
        {{ loading ? "Atualizando..." : "Atualizar" }}
      </button>
    </div>

    <!-- Loading -->
    <div
      v-if="loading"
      class="bg-white shadow rounded-lg p-6 text-center text-slate-500"
    >
      Carregando tarefas...
    </div>

    <!-- Lista -->
    <div v-else>
      <div
        v-if="tasks.length === 0"
        class="bg-white shadow rounded-lg p-6 text-center text-slate-500"
      >
        Nenhuma tarefa encontrada.
      </div>

      <div v-else class="space-y-4">
        <TaskCard
          v-for="t in tasks"
          :key="t.id"
          :task="t"
          @edit="goToEdit"
          @delete="onDelete"
        />
      </div>
    </div>

    <!-- Erro -->
    <p v-if="error" class="text-sm text-red-600 mt-4 whitespace-pre-line">
      {{ error }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import TaskCard from "@/components/TaskCard.vue";
import { useTaskStore } from "../stores/TaskStore";

const router = useRouter();
const store = useTaskStore();

const { tasks } = storeToRefs(store);

const loading = ref(false);
const error = ref<string | null>(null);

function goToCreate() {
  router.push({ path: "/tasks/create" });
}

function goToEdit(task: { id: string }) {
  router.push({ path: `/tasks/${task.id}` });
}

async function reload() {
  loading.value = true;
  error.value = null;

  try {
    await store.loadTasks();
  } catch (err: any) {
    error.value = extractErrorMessage(err) ?? "Erro ao carregar tarefas.";
  } finally {
    loading.value = false;
  }
}

async function onDelete(id: string) {
  error.value = null;
  try {
    await store.deleteTask(id);
  } catch (err: any) {
    error.value = extractErrorMessage(err) ?? "Erro ao excluir a tarefa.";
  }
}

function extractErrorMessage(err: any): string | null {
  const data = err?.response?.data;
  if (!data) return null;
  if (typeof data === "string") return data;
  if (data.message) return data.message;
  return null;
}

onMounted(reload);
</script>

<style scoped></style>
