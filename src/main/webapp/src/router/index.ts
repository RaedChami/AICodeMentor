import { createRouter, createWebHistory } from "vue-router"
import HomeView from "../pages/HomeView.vue"
import ExercisesViewer from "../pages/ExercisesViewer.vue"
import ExerciseDetails from "../pages/ExerciseDetails.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: HomeView },
    { path: "/teacher/generate", component: HomeView },
    { path: "/teacher/exercises", component: ExercisesViewer },
    { path: "/teacher/exercises/:id", component: ExerciseDetails }
  ]
})

export default router
