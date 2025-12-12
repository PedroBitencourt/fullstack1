<template>
  <div class="max-w-3xl mx-auto mt-8">
    <div v-if="loading" class="bg-white shadow-lg rounded-lg p-6">
      <p class="text-slate-600">Carregando tarefa...</p>
    </div>

    <div v-else class="bg-white shadow-lg rounded-lg p-6">
      <div class="flex items-center justify-between gap-4">
        <h2 class="text-2xl font-semibold text-slate-800">Editar Tarefa</h2>

        <div class="flex gap-2">
          <button
            type="button"
            :disabled="isSaving"
            @click="onDelete"
            class="px-3 py-1 rounded-md bg-red-600 text-white hover:bg-red-700 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            Excluir
          </button>

          <button
            type="button"
            :disabled="isSaving"
            @click="goBack"
            class="px-3 py-1 rounded-md border border-slate-200 bg-white hover:bg-slate-50 disabled:opacity-60"
          >
            Voltar
          </button>
        </div>
      </div>

      <form @submit.prevent="onSave" class="mt-4 space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-700" for="title">
            Título <span class="text-red-600">*</span>
          </label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            required
            :disabled="isSaving"
            class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300 disabled:opacity-60"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700" for="description">
            Descrição
          </label>
          <textarea
            id="description"
            v-model="form.description"
            rows="4"
            :disabled="isSaving"
            class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300 disabled:opacity-60"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700" for="status">
            Status <span class="text-red-600">*</span>
          </label>
          <select
            id="status"
            v-model="form.status"
            :disabled="isSaving"
            class="mt-1 w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none disabled:opacity-60"
          >
            <option v-for="s in statusOptions" :key="s.value" :value="s.value">
              {{ s.label }}
            </option>
          </select>
        </div>

        <div class="flex justify-end gap-2">
          <button
            type="submit"
            :disabled="isSaving"
            class="px-4 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            <span v-if="isSaving">Salvando...</span>
            <span v-else>Salvar</span>
          </button>
        </div>

        <p v-if="error" class="mt-2 text-sm text-red-600 whitespace-pre-line">
          {{ error }}
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useTaskStore } from "@/stores/TaskStore";


type TaskStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED";

const statusOptions: { value: TaskStatus; label: string }[] = [
  { value: "PENDING", label: "Pendente" },
  { value: "IN_PROGRESS", label: "Em progresso" },
  { value: "COMPLETED", label: "Concluída" },
];

const route = useRoute();
const router = useRouter();
const store = useTaskStore();

const id = String(route.params.id);

const loading = ref(true);
const isSaving = ref(false);
const error = ref<string | null>(null);

const form = reactive({
  title: "",
  description: "",
  status: "PENDING" as TaskStatus,
});

function goBack() {
  router.push("/tasks");
}

function normalizeDescription(value: string): string | null {
  const trimmed = (value ?? "").trim();
  return trimmed.length ? trimmed : null;
}

function extractErrorMessage(err: any): string | null {
  const data = err?.response?.data;
  if (!data) return null;

  if (typeof data === "string") return data;
  if (data.message) return data.message;

  if (Array.isArray(data.errors)) {
    return data.errors.map((e: any) => e.message ?? String(e)).join("\n");
  }

  return null;
}

async function loadTask() {
  loading.value = true;
  error.value = null;

  try {
    const t = await store.getTaskById(id);
    form.title = t.title ?? "";
    form.description = t.description ?? "";
    form.status = (t.status ?? "PENDING") as TaskStatus;
  } catch (err: any) {
    console.error(err);
    error.value = extractErrorMessage(err) ?? "Erro ao carregar a tarefa.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadTask);

async function onSave() {
  error.value = null;

  const title = form.title.trim();
  if (!title) {
    error.value = "O título é obrigatório.";
    return;
  }

  isSaving.value = true;
  try {
    await store.updateTask(id, {
      title,
      description: normalizeDescription(form.description),
      status: form.status,
    });


    router.push("/tasks");
  } catch (err: any) {
    console.error(err);
    error.value = extractErrorMessage(err) ?? "Erro ao salvar a tarefa.";
  } finally {
    isSaving.value = false;
  }
}

async function onDelete() {
  if (!confirm("Confirma a exclusão desta tarefa?")) return;

  error.value = null;
  isSaving.value = true;
  try {
    await store.deleteTask(id);
    router.push("/tasks");
  } catch (err: any) {
    console.error(err);
    error.value = extractErrorMessage(err) ?? "Erro ao excluir a tarefa.";
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped></style>
