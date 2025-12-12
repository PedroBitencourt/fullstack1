import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: { name: "TaskList" },
  },
  {
    path: "/tasks",
    name: "TaskList",
    component: () => import("@/pages/TaskList.vue"),
  },
  {
    path: "/tasks/create",
    name: "CreateTask",
    component: () => import("@/pages/CreateTask.vue"),
  },
  {
    path: "/tasks/:id",
    name: "EditTask",
    component: () => import("@/pages/UpdateTask.vue"),
    props: true,
    beforeEnter: (to) => {
      if (!to.params.id) {
        return { name: "TaskList" };
      }
    },
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
