// src/stores/TaskStore.ts
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import TaskService from "@/services/TaskService";
import type { TaskResponse, CreateTaskRequest, UpdateTaskRequest } from "@/types/Task";

export const useTaskStore = defineStore("taskStore", () => {
  const tasks = ref<TaskResponse[]>([]);

  const loadingList = ref(false);
  const loadingMutation = ref(false);

  const error = ref<string | null>(null);

  const hasTasks = computed(() => tasks.value.length > 0);

  function setError(err: unknown, fallback: string) {
    error.value = extractErrorMessage(err) ?? fallback;
  }

  function clearError() {
    error.value = null;
  }

  function extractErrorMessage(err: any): string | null {
    const data = err?.response?.data;
    if (!data) return null;
    if (typeof data === "string") return data;
    if (data.message) return data.message;

    // caso seu backend devolva lista de validação
    if (Array.isArray(data.errors)) {
      return data.errors.map((e: any) => e.message ?? String(e)).join("\n");
    }
    return null;
  }

  function normalizeCreatePayload(payload: CreateTaskRequest): CreateTaskRequest {
    const title = payload.title.trim();
    const description = (payload.description ?? "").trim();
    return {
      title,
      description: description.length ? description : null,
    };
  }

  function normalizeUpdatePayload(payload: UpdateTaskRequest): UpdateTaskRequest {
    const title = payload.title.trim();
    const description = (payload.description ?? "").trim();
    return {
      ...payload,
      title,
      description: description.length ? description : null,
    };
  }

  async function loadTasks() {
    loadingList.value = true;
    clearError();

    try {
      tasks.value = await TaskService.getAll();
    } catch (err) {
      setError(err, "Erro ao carregar tarefas");
      throw err; // ✅ deixa a página decidir o que fazer
    } finally {
      loadingList.value = false;
    }
  }

  /**
   * ✅ Busca por id:
   * 1) tenta achar no state local
   * 2) se não achar, busca na API
   * 3) se achar, atualiza/inserir no state
   */
  async function getTaskById(id: string): Promise<TaskResponse> {
    clearError();

    const cached = tasks.value.find((t) => t.id === id);
    if (cached) return cached;

    loadingList.value = true;
    try {
      const task = await TaskService.getById(id);

      // upsert no state
      const idx = tasks.value.findIndex((t) => t.id === task.id);
      if (idx === -1) tasks.value.unshift(task);
      else tasks.value[idx] = task;

      return task;
    } catch (err) {
      setError(err, `Erro ao buscar a tarefa ${id}`);
      throw err;
    } finally {
      loadingList.value = false;
    }
  }

  async function createTask(payload: CreateTaskRequest): Promise<TaskResponse> {
    loadingMutation.value = true;
    clearError();

    try {
      const created = await TaskService.create(normalizeCreatePayload(payload));

      tasks.value = [created, ...tasks.value.filter((t) => t.id !== created.id)];
      return created;
    } catch (err) {
      setError(err, "Erro ao criar tarefa");
      throw err;
    } finally {
      loadingMutation.value = false;
    }
  }

  async function updateTask(id: string, payload: UpdateTaskRequest): Promise<TaskResponse> {
    loadingMutation.value = true;
    clearError();

    try {
      const updated = await TaskService.update(id, normalizeUpdatePayload(payload));

      const idx = tasks.value.findIndex((t) => t.id === id);
      if (idx !== -1) tasks.value[idx] = updated;
      else tasks.value.unshift(updated);

      return updated;
    } catch (err) {
      setError(err, "Erro ao atualizar tarefa");
      throw err;
    } finally {
      loadingMutation.value = false;
    }
  }

  async function deleteTask(id: string): Promise<void> {
    loadingMutation.value = true;
    clearError();

    try {
      await TaskService.delete(id);
      tasks.value = tasks.value.filter((t) => t.id !== id);
    } catch (err) {
      setError(err, "Erro ao excluir tarefa");
      throw err;
    } finally {
      loadingMutation.value = false;
    }
  }

  return {
    tasks,
    hasTasks,
    loadingList,
    loadingMutation,
    error,
    loadTasks,
    getTaskById,
    createTask,
    updateTask,
    deleteTask,
    clearError,
  };
});
