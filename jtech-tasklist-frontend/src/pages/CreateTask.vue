<template>
  <div class="max-w-3xl mx-auto mt-8">
    <div class="bg-white shadow-lg rounded-lg p-6">
      <h2 class="text-2xl font-semibold text-slate-800 mb-4">Nova Tarefa</h2>

      <form @submit.prevent="onSubmit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-700" for="title">
            Título <span class="text-red-600">*</span>
          </label>

          <input
            id="title"
            ref="titleInput"
            v-model="form.title"
            type="text"
            required
            :disabled="isSubmitting"
            class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300 disabled:opacity-60"
            placeholder="Título da tarefa"
            maxlength="255"
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
            :disabled="isSubmitting"
            class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300 disabled:opacity-60"
            placeholder="Detalhes (opcional)"
          />
        </div>

        <div class="flex items-center justify-end gap-2">
          <button
            type="button"
            @click="goBack"
            :disabled="isSubmitting"
            class="px-4 py-2 rounded-md border border-slate-200 bg-white hover:bg-slate-50 disabled:opacity-60"
          >
            Cancelar
          </button>

          <button
            type="submit"
            :disabled="isSubmitting"
            class="px-4 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            <span v-if="isSubmitting">Salvando...</span>
            <span v-else>Salvar</span>
          </button>
        </div>
      </form>

      <p v-if="error" class="mt-4 text-sm text-red-600 whitespace-pre-line">
        {{ error }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useTaskStore } from "@/stores/TaskStore";

const router = useRouter();
const store = useTaskStore();

const titleInput = ref<HTMLInputElement | null>(null);

const form = reactive({
  title: "",
  description: "",
});

const isSubmitting = ref(false);
const error = ref<string | null>(null);

onMounted(async () => {
  await nextTick();
  titleInput.value?.focus();
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

  // Caso seu backend devolva lista de erros
  if (Array.isArray(data.errors)) {
    return data.errors.map((e: any) => e.message ?? String(e)).join("\n");
  }

  return null;
}

async function onSubmit() {
  error.value = null;

  const title = form.title.trim();
  if (!title) {
    error.value = "O título é obrigatório.";
    titleInput.value?.focus();
    return;
  }
  console.log("chegou aqui")
  isSubmitting.value = true;
  try {
    await store.createTask({
      title,
      description: normalizeDescription(form.description),
    });


    router.push("/tasks");
  } catch (err: any) {
    console.error(err);
    error.value = extractErrorMessage(err) ?? "Erro ao criar tarefa.";
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped></style>
