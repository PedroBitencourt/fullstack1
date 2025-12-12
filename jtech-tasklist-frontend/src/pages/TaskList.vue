<template>
  <div class="max-w-5xl mx-auto mt-8">
    <!-- Cabeçalho -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-slate-800">Minhas Tarefas</h1>

      <button
        type="button"
        @click="openCreate()"
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
          @edit="openEdit"
          @delete="openDelete"
        />
      </div>
    </div>

    <!-- Erro geral -->
    <p v-if="error" class="text-sm text-red-600 mt-4 whitespace-pre-line">
      {{ error }}
    </p>
  </div>

  <!-- MODAL: Criar tarefa -->
  <BaseModal :open="createOpen" @close="closeCreate">
    <template #title>Nova tarefa</template>

    <form @submit.prevent="saveCreate" class="grid gap-3">
      <label class="grid gap-1">
        <span class="text-sm text-slate-700">Título *</span>
        <input
          v-model="createForm.title"
          :disabled="savingCreate"
          required
          maxlength="255"
          class="w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
          placeholder="Título da tarefa"
        />
      </label>

      <label class="grid gap-1">
        <span class="text-sm text-slate-700">Descrição</span>
        <textarea
          v-model="createForm.description"
          rows="4"
          :disabled="savingCreate"
          class="w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
          placeholder="Detalhes (opcional)"
        />
      </label>

      <p v-if="createError" class="text-sm text-red-600 whitespace-pre-line">{{ createError }}</p>
    </form>

    <template #footer>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md border border-slate-200 hover:bg-slate-50"
        :disabled="savingCreate"
        @click="closeCreate"
      >
        Cancelar
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-60"
        :disabled="savingCreate"
        @click="saveCreate"
      >
        {{ savingCreate ? "Salvando..." : "Salvar" }}
      </button>
    </template>
  </BaseModal>

  <!-- MODAL: Editar tarefa -->
  <BaseModal :open="editOpen" @close="closeEdit">
    <template #title>Editar tarefa</template>

    <form @submit.prevent="saveEdit" class="grid gap-3">
      <label class="grid gap-1">
        <span class="text-sm text-slate-700">Título *</span>
        <input
          v-model="editForm.title"
          :disabled="savingEdit"
          required
          maxlength="255"
          class="w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
        />
      </label>

      <label class="grid gap-1">
        <span class="text-sm text-slate-700">Descrição</span>
        <textarea
          v-model="editForm.description"
          rows="4"
          :disabled="savingEdit"
          class="w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
        />
      </label>

      <label class="grid gap-1">
        <span class="text-sm text-slate-700">Status *</span>
        <select
          v-model="editForm.status"
          :disabled="savingEdit"
          class="w-full rounded-md border border-slate-200 px-3 py-2 focus:outline-none"
        >
          <option v-for="s in statusOptions" :key="s.value" :value="s.value">
            {{ s.label }}
          </option>
        </select>
      </label>

      <p v-if="editError" class="text-sm text-red-600 whitespace-pre-line">{{ editError }}</p>
    </form>

    <template #footer>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md border border-slate-200 hover:bg-slate-50"
        :disabled="savingEdit"
        @click="closeEdit"
      >
        Cancelar
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-60"
        :disabled="savingEdit"
        @click="saveEdit"
      >
        {{ savingEdit ? "Salvando..." : "Salvar" }}
      </button>
    </template>
  </BaseModal>

  <!-- MODAL: Confirmar exclusão -->
  <ConfirmDialog
    :open="confirmOpen"
    title="Excluir tarefa"
    message="Tem certeza que deseja excluir esta tarefa?"
    confirm-label="Excluir"
    cancel-label="Cancelar"
    :loading="deleting"
    @confirm="confirmDelete"
    @cancel="confirmOpen = false"
  />
</template>

<script setup lang="ts">
import { onMounted, ref, reactive, nextTick } from "vue";
import { storeToRefs } from "pinia";
import TaskCard from "@/components/TaskCard.vue";
import BaseModal from "@/components/BaseModal.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import { useTaskStore } from "@/stores/TaskStore";

type TaskStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED"; // ajuste se seu backend usar outros nomes

const statusOptions: { value: TaskStatus; label: string }[] = [
  { value: "PENDING", label: "Pendente" },
  { value: "IN_PROGRESS", label: "Em progresso" },
  { value: "COMPLETED", label: "Concluída" },
];

const store = useTaskStore();
const { tasks } = storeToRefs(store);

const loading = ref(false);
const error = ref<string | null>(null);

/* ---------- Lista ---------- */
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
onMounted(reload);

/* ---------- Criar (modal) ---------- */
const createOpen = ref(false);
const savingCreate = ref(false);
const createError = ref<string | null>(null);
const createForm = reactive({
  title: "",
  description: "",
});

function openCreate() {
  createError.value = null;
  createForm.title = "";
  createForm.description = "";
  createOpen.value = true;
  nextTick(() => {
    // opcional: focar no primeiro input
    (document.querySelector("#create-title") as HTMLInputElement)?.focus?.();
  });
}
function closeCreate() { createOpen.value = false; }

function normalizeDesc(v: string): string | null {
  const t = (v ?? "").trim();
  return t.length ? t : null;
}

async function saveCreate() {
  createError.value = null;
  const title = createForm.title.trim();
  if (!title) { createError.value = "O título é obrigatório."; return; }

  savingCreate.value = true;
  try {
    await store.createTask({ title, description: normalizeDesc(createForm.description) });
    closeCreate(); // store já faz push no state
  } catch (err: any) {
    createError.value = extractErrorMessage(err) ?? "Erro ao criar tarefa.";
  } finally {
    savingCreate.value = false;
  }
}

/* ---------- Editar (modal) ---------- */
const editOpen = ref(false);
const savingEdit = ref(false);
const editError = ref<string | null>(null);
const editForm = reactive({
  id: "" as string,
  title: "",
  description: "",
  status: "PENDING" as TaskStatus,
});

function openEdit(task: { id: string }) {
  editError.value = null;
  const t = tasks.value.find((x) => x.id === task.id);
  if (!t) { editError.value = "Tarefa não encontrada na lista."; return; }

  editForm.id = t.id;
  editForm.title = t.title ?? "";
  editForm.description = t.description ?? "";
  editForm.status = (t.status ?? "PENDING") as TaskStatus;
  editOpen.value = true;
}

function closeEdit() { editOpen.value = false; }

async function saveEdit() {
  editError.value = null;
  const title = editForm.title.trim();
  if (!title) { editError.value = "O título é obrigatório."; return; }

  savingEdit.value = true;
  try {
    await store.updateTask(editForm.id, {
      title,
      description: normalizeDesc(editForm.description),
      status: editForm.status,
    });
    closeEdit(); // store já atualiza no state
  } catch (err: any) {
    editError.value = extractErrorMessage(err) ?? "Erro ao salvar a tarefa.";
  } finally {
    savingEdit.value = false;
  }
}

/* ---------- Excluir (modal) ---------- */
const confirmOpen = ref(false);
const deleting = ref(false);
const selectedId = ref<string | null>(null);

function openDelete(id: string) {
  selectedId.value = id;
  confirmOpen.value = true;
}

async function confirmDelete() {
  if (!selectedId.value) return;
  deleting.value = true;
  error.value = null;

  try {
    await store.deleteTask(selectedId.value);
    confirmOpen.value = false;
  } catch (err: any) {
    error.value = extractErrorMessage(err) ?? "Erro ao excluir a tarefa.";
  } finally {
    deleting.value = false;
    selectedId.value = null;
  }
}

/* ---------- utils ---------- */
function extractErrorMessage(err: any): string | null {
  const data = err?.response?.data;
  if (!data) return null;
  if (typeof data === "string") return data;
  if (data.message) return data.message;
  if (Array.isArray(data.errors)) return data.errors.map((e: any) => e.message ?? String(e)).join("\n");
  return null;
}
</script>

<style scoped></style>
