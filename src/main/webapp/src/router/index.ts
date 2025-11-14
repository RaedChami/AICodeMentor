import { createRouter, createWebHistory } from "vue-router"
import HomeView from "../pages/HomeView.vue"
import ExerciseEditor from "../pages/ExerciseEditor.vue"
import ExercisesViewer from "../pages/ExercisesViewer.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: HomeView },
    { path: "/teacher/generate", component: HomeView },
    { path: "/teacher/generate/exercises", component: ExercisesViewer },
    { path: "/teacher/generate/exercises/editor", component: ExerciseEditor }
  ]
})

export default router
