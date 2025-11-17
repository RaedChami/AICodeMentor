<template>
  <div>
    <Navbar />

    <div class="container-fluid mt-4">
      <div class="row justify-content-center">
        <div class="col-lg-100 col-xl-200">

          <div class="card shadow-sm border-0">
            <div class="card-body p-5">


                <button @click="createExercise"
                        class="btn btn-primary mb-3 px-4 py-2">
                  Créer un exercice
                </button>

                <form @submit.prevent="seeExercise">
                  <button type="submit" class="btn btn-primary">
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
import Navbar from '../components/NavBar.vue'
import { useRouter } from "vue-router"

const router = useRouter()

function createExercise() {
  router.push("/teacher/generate")
}

function seeExercise() {
  router.push("/teacher/exercises")
}
</script>
