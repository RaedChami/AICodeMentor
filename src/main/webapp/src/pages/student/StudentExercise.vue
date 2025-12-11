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

        <!-- Partie droite : code + tests -->
        <div class="col-lg-8">

          <!-- Signature -->
          <div class="card shadow-sm mb-3">
            <div class="card-body">
              <pre class="bg-light p-3 rounded"><code>{{ exercise.signatureAndBody }}</code></pre>
            </div>
          </div>

          <!-- Zone de code étudiant -->
          <div class="card shadow-sm mb-3">
            <div class="card-header bg-secondary text-white">
              <h5 class="mb-0">Votre code</h5>
            </div>
            <div class="card-body">
              <textarea
                v-model="studentCode"
                class="form-control font-monospace"
                rows="12"
                placeholder="Écrivez votre code Java ici..."
              ></textarea>

              <button
                class="btn btn-success mt-3"
                @click="runTests"
              >
                Lancer les tests
              </button>
            </div>
          </div>

          <!-- Résultats des tests -->
          <div class="card shadow-sm" v-if="testResult !== null">
            <div class="card-header bg-info text-white">
              <h5 class="mb-0">Résultat des tests</h5>
            </div>
            <div class="mt-3" v-if="showHintButton">
              <button class="btn btn-warning" @click="showHints">
                Donner des indices
              </button>
            </div>

            <div v-if="hintLoading" class="text-center my-3">
              <div
                class="spinner-border text-info mb-3"
                style="width: 3rem; height: 3rem;"
                role="status"
              >
                <span class="visually-hidden">Chargement...</span>
              </div>
              <p class="text-muted mb-0">Recherche d'un indice...</p>
            </div>
            <div v-if="!hintLoading && hints" class="alert alert-warning mt-3">
              <pre>{{ hints }}</pre>
            </div>

            <div class="card-body">
              <pre class="bg-light p-3 rounded">{{ testResult }}</pre>
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

const route = useRoute()
const router = useRouter()

const exercise = ref<any>(null)
const loading = ref(true)

const studentCode = ref("")
const testResult = ref<string | null>(null)

const showHintButton = ref(false)
const hints = ref<string | null>(null)

const hintLoading = ref(false)

async function runTests() {
  testResult.value = "Exécution des tests en cours..."

  hints.value = null

  const res = await fetch(`/api/student/exercises/run-tests/${exercise.value.id}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ code: studentCode.value })
  })

  const data = await res.text()
  testResult.value = data
  showHintButton.value = true
}

async function fetchExercise() {
  try {
    loading.value = true
    const id = route.params.id
    const res = await fetch(`/api/teacher/exercises/${id}`)
    exercise.value = res.ok ? await res.json() : null
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

async function showHints() {
  hintLoading.value = true
  const res = await fetch(`/api/student/exercises/get-hint/${exercise.value.id}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ code: studentCode.value })
  })

  hints.value = await res.text()
  hintLoading.value = false
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
