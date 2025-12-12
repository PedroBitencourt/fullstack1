// src/router/index.ts
import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  { path: "/", redirect: { name: "TaskList" } },
  {
    path: "/tasks",
    name: "TaskList",
    component: () => import("@/pages/TaskList.vue"),
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
