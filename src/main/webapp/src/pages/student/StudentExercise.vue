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
      <div v-if="mode === 'view'">
        <div class="d-flex justify-content-end gap-2 mb-3">
          <button @click="goBack" class="btn btn-primary me-auto bg-primary">
            Retour
          </button>
        </div>
        <div class="row g-4">
            <div class="col-lg-4">
              <div class="card shadow-sm mb-3">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0">Caractéristiques</h5>
                </div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label fw-bold text-light">Énoncé:</label>
                    <p class="text-light">{{ exercise.description }}</p>
                  </div>

                  <div class="mb-3">
                    <label class="form-label fw-bold text-light">Difficulté:</label>
                    <p class="text-light">{{ exercise.difficulty }}</p>
                  </div>

                  <div class="mb-3">
                    <label class="form-label fw-bold text-light">Concepts:</label>
                    <ul class="list-unstyled">
                      <li v-for="(concept, index) in exercise.concepts" :key="index" class="mb-1">
                        <span class="badge bg-secondary">{{ concept }}</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-lg-8">
              <div class="card shadow-sm mb-3">
                <div class="card-body">
                  <pre class="bg-light p-3 rounded"><code>{{ exercise.signatureAndBody }}</code></pre>
                </div>
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
const mode = ref('view')

async function fetchExercise() {
  try {
    loading.value = true
    const id = route.params.id
    const res = await fetch(`/api/teacher/exercises/${id}`)
    if (res.ok) {
      const data = await res.json()
      exercise.value = data
    } else {
      exercise.value = null
    }
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
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

code {
  color: #212529;
}

.card {
  border: none;
}

.card-header h5 {
  font-size: 1rem;
  font-weight: 600;
}

.badge {
  font-size: 0.85rem;
  padding: 0.4rem 0.8rem;
}
</style>