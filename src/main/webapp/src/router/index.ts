import { createRouter, createWebHistory } from "vue-router"
import GenerateView from "../pages/GenerateView.vue"
import TeacherView from "../pages/TeacherView.vue"
import StudentView from "../pages/StudentView.vue"
import LoginView from "../pages/LoginView.vue"
import CreateAccountView from "../pages/CreateAccountView.vue"
import ExercisesViewer from "../pages/ExercisesViewer.vue"
import ModifyExercise from "../pages/ModifyExercise.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: LoginView },
    { path: "/teacher", component: TeacherView },
    { path: "/student", component: StudentView },
    { path: "/login", component: LoginView },
    { path: "/create-account", component: CreateAccountView },
    { path: "/teacher/generate", component: GenerateView },
    { path: "/teacher/exercises", component: ExercisesViewer },
    { path: "/student/exercises", component: ExercisesViewer },
    { path: "/teacher/exercises/:id", component: ModifyExercise }
  ]
})

export default router
