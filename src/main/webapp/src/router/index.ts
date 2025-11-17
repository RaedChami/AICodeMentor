import { createRouter, createWebHistory } from "vue-router"
import HomeView from "../pages/HomeView.vue"
import TeacherView from "../pages/TeacherView.vue"
import StudentView from "../pages/StudentView.vue"
import LoginView from "../pages/LoginView.vue"
import CreateAccountView from "../pages/CreateAccountView.vue"
import CreateExerciseView from "../pages/CreateExerciseView.vue"
import ExercisesViewer from "../pages/ExercisesViewer.vue"
import ExerciseDetails from "../pages/ExerciseDetails.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: HomeView },
    { path: "/teacher", component: TeacherView },
    { path: "/student", component: StudentView },
    { path: "/login", component: LoginView },
    { path: "/create-account", component: CreateAccountView },
    { path: "/teacher/generate", component: CreateExerciseView },
    { path: "/teacher/exercises", component: ExercisesViewer },
    { path: "/teacher/exercises/:id", component: ExerciseDetails }
  ]
})

export default router
