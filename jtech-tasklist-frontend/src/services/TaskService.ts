import { http } from "@/services/Http";
import type {
  TaskResponse,
  CreateTaskRequest,
  UpdateTaskRequest,
} from "@/types/Task";

const BASE_PATH = "/api/v1/tasks";

export const TaskService = {
  async getAll(): Promise<TaskResponse[]> {
    const { data } = await http.get<TaskResponse[]>(BASE_PATH);
    return data;
  },

  async getById(id: string): Promise<TaskResponse> {
    const { data } = await http.get<TaskResponse>(`${BASE_PATH}/${id}`);
    return data;
  },

  async create(payload: CreateTaskRequest): Promise<TaskResponse> {
    const { data } = await http.post<TaskResponse>(BASE_PATH, payload);
    return data;
  },

  async update(
    id: string,
    payload: UpdateTaskRequest
  ): Promise<TaskResponse> {
    const { data } = await http.put<TaskResponse>(
      `${BASE_PATH}/${id}`,
      payload
    );
    return data;
  },

  async delete(id: string): Promise<void> {
    await http.delete(`${BASE_PATH}/${id}`);
  },
};

export default TaskService;
