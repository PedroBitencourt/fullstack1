export type TaskStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED";
export interface Task {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
  statusDescription: string;
  createdAt: string;
  updatedAt: string;
}

export interface TaskResponse {
  id: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  statusDescription?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateTaskRequest {
  title: string;
  description: string | null;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string | null;
  status?: string;
}
