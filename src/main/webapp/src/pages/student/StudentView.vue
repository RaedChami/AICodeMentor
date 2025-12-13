<template>
    <div>
        <StudentNavBar />
        <div class="container-fluid">
          <div class="row justify-content-center">
            <div class="col-lg-100 col-xl-200">

              <div class="card shadow-sm border-0">
                <div class="card-body p-5">
                    <button @click="seeExercisesSubmitted"
                        class="btn btn-info mb-3 px-4 py-2">
                      Continuer les exercices en cours
                    </button>

                    <form @submit.prevent="seeExercise">
                      <button type="submit" class="btn btn-info">
                        Consulter les exercices
                      </button>
                    </form>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import StudentNavBar from '../../components/StudentNavBar.vue'
import { useRouter } from "vue-router"
const router = useRouter()
async function seeExercise() {
    router.push("/student/exercises")
}
async function seeExercisesSubmitted() {
    router.push("/student/exercises-submitted")
}
</script>