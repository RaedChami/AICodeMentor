import { createRouter, createWebHistory } from "vue-router"
import GenerateView from "../pages/teacher/GenerateView.vue"
import TeacherView from "../pages/teacher/TeacherView.vue"
import StudentView from "../pages/student/StudentView.vue"
import LoginView from "../pages/LoginView.vue"
import CreateAccountView from "../pages/CreateAccountView.vue"
import TeacherExercisesViewer from "../pages/teacher/TeacherExercisesViewer.vue"
import StudentExercisesViewer from "../pages/student/StudentExercisesViewer.vue"
import ModifyExercise from "../pages/teacher/ModifyExercise.vue"
import StudentExercise from "../pages/student/StudentExercise.vue"
import StudentExercisesSubmitted from "../pages/student/StudentExercisesSubmittedView.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: LoginView },
    { path: "/teacher", component: TeacherView },
    { path: "/student", component: StudentView },
    { path: "/login", component: LoginView },
    { path: "/create-account", component: CreateAccountView },
    { path: "/teacher/generate", component: GenerateView },
    { path: "/teacher/exercises", component: TeacherExercisesViewer },
    { path: "/student/exercises", component: StudentExercisesViewer },
    { path: "/teacher/exercises/:id", component: ModifyExercise },
    { path: "/student/exercises/:id", component: StudentExercise },
    { path: "/student/exercises-submitted", component: StudentExercisesSubmitted }
  ]
})

export default router
