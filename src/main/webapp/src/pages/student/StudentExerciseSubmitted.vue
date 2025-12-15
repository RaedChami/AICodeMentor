<template>
  <DecoBar />
  <div class="container-fluid py-4">
    <div class="row mb-4">
      <div class="col">
        <h2 class="mb-0">Exercice {{ exercise?.id }}</h2>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement…</span>
      </div>
    </div>

    <div v-else-if="exercise">
      <div class="d-flex justify-content-end gap-2 mb-3">
        <button @click="goBack" class="btn btn-primary me-auto bg-primary">
          Retour
        </button>
      </div>

      <div class="row g-4">

        <!-- Partie gauche : infos exercice -->
        <div class="col-lg-4">
          <div class="card shadow-sm mb-3">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0">Caractéristiques</h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <label class="form-label fw-bold text-light">Énoncé :</label>
                <p class="text-light">{{ exercise.description }}</p>
              </div>

              <div class="mb-3">
                <label class="form-label fw-bold text-light">Difficulté :</label>
                <p class="text-light">{{ exercise.difficulty }}</p>
              </div>

              <div class="mb-3">
                <label class="form-label fw-bold text-light">Concepts :</label>
                <ul class="list-unstyled">
                  <li
                    v-for="(concept, index) in exercise.concepts"
                    :key="index"
                    class="mb-1"
                  >
                    <span class="badge bg-secondary">{{ concept }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-8">
          <div class="card shadow-sm mb-3">
            <div class="card-header bg-secondary text-white">
              <h5 class="mb-0">Votre code</h5>
            </div>
            <div class="card-body">
              <MonacoEditor
                v-model="studentCode"
                language="java"
                height="450px"
                :read-only="true"
                theme="vs-dark"
              />
            </div>
          </div>

        </div>
      </div>
    </div>

    <div v-else class="alert alert-warning" role="alert">
      <p class="mb-0">Exercice introuvable</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DecoBar from '../../components/DecoBar.vue'
import MonacoEditor from '../../components/MonacoEditor.vue'

const route = useRoute()
const router = useRouter()

const exercise = ref<any>(null)
const loading = ref(true)

const studentCode = ref("")

const user = JSON.parse(sessionStorage.getItem("user") || "{}")


async function fetchExercise() {
  try {
    loading.value = true
    const id = Number(route.params.id)

    const res = await fetch(`/api/teacher/exercises/${id}`)
    exercise.value = res.ok ? await res.json() : null

    if (exercise.value) {
      studentCode.value = exercise.value.signatureAndBody
      await fetchSubmittedSolution(exercise.value.id)
    }
  } finally {
    loading.value = false
  }
}


function goBack() {
  router.back()
}

async function fetchSubmittedSolution(exerciseId: number) {
  if (!user.id) return

  const res = await fetch(
    `/api/student/exercises-submitted/solution?loginId=${user.id}&exerciseId=${exerciseId}`
  )

  if (res.ok) {
    const code = await res.text()
    if (code) {
      studentCode.value = code
    }
  }
}



onMounted(fetchExercise)
</script>

<style scoped>
.font-monospace {
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9rem;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
