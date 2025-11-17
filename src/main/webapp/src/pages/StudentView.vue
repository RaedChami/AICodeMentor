<template>
    <div>
        <Navbar />
        <div class="container-fluid">
          <div class="row justify-content-center">
            <div class="col-lg-100 col-xl-200">

              <div class="card shadow-sm border-0">
                <div class="card-body p-5">
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
async function seeExercise() {
    router.push("/teacher/exercises")
}
</script>