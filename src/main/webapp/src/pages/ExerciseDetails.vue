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
          <button @click="mode = 'modify'" class="btn btn-warning">
            Modifier
          </button>
          <button class="btn btn-danger" @click="deleteExercise(exercise.id)">
            Supprimer
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
                <div class="card-header bg-success text-white">
                  <h5 class="mb-0">Signature & Corps</h5>
                </div>
                <div class="card-body">
                  <pre class="bg-light p-3 rounded"><code>{{ exercise.signatureAndBody }}</code></pre>
                </div>
              </div>

              <div class="card shadow-sm mb-3">
                <div class="card-header bg-info text-white">
                  <h5 class="mb-0">Tests JUnit</h5>
                </div>
                <div class="card-body">
                  <pre class="bg-light p-3 rounded"><code>{{ exercise.unitTests }}</code></pre>
                </div>
              </div>

              <div class="card shadow-sm">
                <div class="card-header bg-warning text-dark">
                  <h5 class="mb-0">Solution</h5>
                </div>
                <div class="card-body">
                  <pre class="bg-light p-3 rounded"><code>{{ exercise.solution }}</code></pre>
                </div>
              </div>
            </div>
          </div>

        </div>

      <div v-else-if="mode === 'modify'">
        <form @submit.prevent="updateExercise">
          <div v-if="validationErrors.length > 0" class="alert alert-danger" role="alert">
            <h5 class="alert-heading">Veuillez corriger les erreurs suivantes :</h5>
            <ul class="mb-0">
              <li v-for="(error, index) in validationErrors" :key="index">{{ error }}</li>
            </ul>
          </div>

          <div class="row g-4">
            <div class="col-lg-4">
              <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0">Caractéristiques</h5>
                </div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label fw-bold">Énoncé:</label>
                    <input
                      v-model="exercise.description"
                      type="text"
                      class="form-control"
                      :class="{ 'is-invalid': !exercise.description?.trim() && showErrors }"
                      required
                    />
                  </div>

                  <div class="mb-3">
                    <label class="form-label fw-bold">Difficulté:</label>
                    <input
                      v-model="exercise.difficulty"
                      type="text"
                      class="form-control"
                      :class="{ 'is-invalid': !exercise.difficulty?.trim() && showErrors }"
                      required
                    />
                  </div>

                  <div class="mb-3">
                    <label class="form-label fw-bold">Concepts:</label>
                    <div v-for="(_concept, index) in exercise.concepts" :key="index" class="input-group mb-2">
                      <input
                        v-model="exercise.concepts[index]"
                        type="text"
                        class="form-control"
                        :class="{ 'is-invalid': !exercise.concepts[index]?.trim() && showErrors }"
                        placeholder="Nom du concept"
                        required
                      />
                      <button type="button" class="btn btn-outline-danger" @click="removeConcept(index)">
                        X
                      </button>
                    </div>
                    <button type="button" class="btn btn-sm btn-outline-primary w-100" @click="addConcept">
                      Ajouter un concept
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-lg-8">
              <div class="card shadow-sm mb-3">
                <div class="card-header bg-success text-white">
                  <h5 class="mb-0">Signature & Corps</h5>
                </div>
                <div class="card-body">
                  <textarea
                    v-model="exercise.signatureAndBody"
                    class="form-control font-monospace"
                    :class="{ 'is-invalid': !exercise.signatureAndBody?.trim() && showErrors }"
                    rows="12"
                    required
                  ></textarea>
                </div>
              </div>

              <div class="card shadow-sm mb-3">
                <div class="card-header bg-info text-white">
                  <h5 class="mb-0">Tests JUnit</h5>
                </div>
                <div class="card-body">
                  <textarea
                    v-model="exercise.unitTests"
                    class="form-control font-monospace"
                    :class="{ 'is-invalid': !exercise.unitTests?.trim() && showErrors }"
                    rows="12"
                    required
                  ></textarea>
                </div>
              </div>

              <div class="card shadow-sm mb-3">
                <div class="card-header bg-warning text-dark">
                  <h5 class="mb-0">Solution</h5>
                </div>
                <div class="card-body">
                  <textarea
                    v-model="exercise.solution"
                    class="form-control font-monospace"
                    :class="{ 'is-invalid': !exercise.solution?.trim() && showErrors }"
                    rows="12"
                    required
                  ></textarea>
                </div>
              </div>
            </div>
          </div>

          <div class="row mt-4">
            <div class="col">
              <div class="d-flex gap-2 justify-content-end">
                <button type="button" class="btn btn-secondary" @click="cancelEdit">
                  Annuler
                </button>
                <button type="submit" class="btn btn-success">
                  Enregistrer
                </button>
              </div>
            </div>
          </div>
        </form>
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
import DecoBar from '../components/DecoBar.vue'

const route = useRoute()
const router = useRouter()
const exercise = ref<any>(null)
const loading = ref(true)
const mode = ref<'view' | 'modify'>('view')
const validationErrors = ref<string[]>([])
const showErrors = ref(false)

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

async function deleteExercise(id: number) {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cet exercice ?')) {
    return
  }

  const res = await fetch(`/api/teacher/exercises/${id}`, { method: "DELETE" })
  if (res.ok) {
    router.push('/teacher/exercises')
  } else {
    alert('Impossible de supprimer')
  }
}

function validateExercise(): boolean {
  validationErrors.value = []
  showErrors.value = true

  if (!exercise.value.description?.trim()) {
    validationErrors.value.push("La description est obligatoire")
  }

  if (!exercise.value.difficulty?.trim()) {
    validationErrors.value.push("La difficulté est obligatoire")
  }

  if (!exercise.value.concepts || exercise.value.concepts.length === 0) {
    validationErrors.value.push("Au moins un concept est requis")
  } else {
    const emptyConcepts = exercise.value.concepts.filter((c: string) => !c?.trim())
    if (emptyConcepts.length > 0) {
      validationErrors.value.push("Tous les concepts doivent être renseignés (champs vides détectés)")
    }
  }

  if (!exercise.value.signatureAndBody?.trim()) {
    validationErrors.value.push("La signature et le corps sont obligatoires")
  }

  if (!exercise.value.unitTests?.trim()) {
    validationErrors.value.push("Les tests JUnit sont obligatoires")
  }

  if (!exercise.value.solution?.trim()) {
    validationErrors.value.push("La solution est obligatoire")
  }

  return validationErrors.value.length === 0
}

async function updateExercise() {
  if (!validateExercise()) {
    window.scrollTo({ top: 0, behavior: 'smooth' })
    return
  }

  const res = await fetch(`/api/teacher/exercises/${exercise.value.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(exercise.value)
  })

  if (res.ok) {
    const updated = await res.json()
    exercise.value = updated
    mode.value = 'view'
    validationErrors.value = []
    showErrors.value = false
    alert('Exercice mis à jour avec succès !')
  } else {
    alert('Erreur de sauvegarde')
  }
}

function addConcept() {
  if (!exercise.value.concepts) {
    exercise.value.concepts = []
  }
  exercise.value.concepts.push("")
}

function removeConcept(index: number) {
  exercise.value.concepts.splice(index, 1)
}

function cancelEdit() {
  validationErrors.value = []
  showErrors.value = false
  mode.value = 'view'
  fetchExercise()
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