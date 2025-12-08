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
            Modifier avec IA
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
        <form @submit.prevent="modifyWithLLM">
          <div v-if="validationErrors.length > 0" class="alert alert-danger" role="alert">
            <h5 class="alert-heading">Veuillez corriger les erreurs suivantes :</h5>
            <ul class="mb-0">
              <li v-for="(error, index) in validationErrors" :key="index">{{ error }}</li>
            </ul>
          </div>

          <div class="alert alert-info" role="alert">
            <h5 class="alert-heading">
              <i class="bi bi-robot"></i> Modification assistée par IA
            </h5>
            <p class="mb-0">
              Décrivez les modifications que vous souhaitez apporter à cet exercice.
            </p>
          </div>

          <div class="row g-4">
            <div class="col-lg-12">
              <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0">Instructions de modification</h5>
                </div>
                <div class="card-body">
                  <textarea
                    v-model="modificationDescription"
                    class="form-control"
                    :class="{ 'is-invalid': !modificationDescription?.trim() && showErrors }"
                    rows="6"
                    placeholder="Exemple : Changer la difficulté de l'exercice, ajouter un test unitaire à la classe de tests, utiliser un stream pour la solution..."
                    required
                  ></textarea>
                  <div class="invalid-feedback">
                    Veuillez décrire les modifications souhaitées
                  </div>
                </div>
              </div>
            </div>

            <div class="col-lg-12">
              <div class="card shadow-sm bg-light">
                <div class="card-header bg-secondary text-white">
                  <h5 class="mb-0">Aperçu de l'exercice actuel</h5>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-6 text-white">
                      <p><strong>Énoncé:</strong> {{ exercise.description }}</p>
                      <p><strong>Difficulté:</strong> {{ exercise.difficulty }}</p>
                      <p><strong>Concepts:</strong> {{ exercise.concepts?.join(', ') }}</p>
                    </div>
                    <div class="col-md-6 text-white">
                      <p><strong>Signature:</strong></p>
                      <pre class="small bg-white p-2 rounded"><code>{{ exercise.signatureAndBody?.substring(0, 200) }}</code></pre>
                      <p><strong>Tests Unitaires:</strong></p>
                      <pre class="small bg-white p-2 rounded"><code>{{ exercise.unitTests?.substring(0, 200) }}</code></pre>
                      <p><strong>Solution:</strong></p>
                      <pre class="small bg-white p-2 rounded"><code>{{ exercise.solution?.substring(0, 200) }}</code></pre>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="row mt-4">
            <div class="col">
              <div class="d-flex gap-2 justify-content-end">
                <button type="button" class="btn btn-secondary" @click="cancelEdit" :disabled="isModifying">
                  Annuler
                </button>
                <button type="submit" class="btn btn-success" :disabled="isModifying">
                  <span v-if="isModifying" class="spinner-border spinner-border-sm me-2" role="status">
                    <span class="visually-hidden">Génération en cours...</span>
                  </span>
                  <i v-else class="bi bi-magic me-2"></i>
                  {{ isModifying ? 'Génération en cours...' : 'Générer avec IA' }}
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
import DecoBar from '../../components/DecoBar.vue'

const route = useRoute()
const router = useRouter()
const exercise = ref<any>(null)
const loading = ref(true)
const mode = ref<'view' | 'modify'>('view')
const validationErrors = ref<string[]>([])
const showErrors = ref(false)
const modificationDescription = ref('')
const isModifying = ref(false)

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

function validateModification(): boolean {
  validationErrors.value = []
  showErrors.value = true

  if (!modificationDescription.value?.trim()) {
    validationErrors.value.push("La description des modifications est obligatoire")
  }

  return validationErrors.value.length === 0
}

async function modifyWithLLM() {
  if (!validateModification()) {
    window.scrollTo({ top: 0, behavior: 'smooth' })
    return
  }

  try {
    isModifying.value = true
    const res = await fetch(`/api/teacher/exercises/${exercise.value.id}/modify`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prompt: modificationDescription.value
      })
    })

    if (res.ok) {
      const updated = await res.json()
      exercise.value = updated
      mode.value = 'view'
      modificationDescription.value = ''
      validationErrors.value = []
      showErrors.value = false
      alert('Exercice modifié avec succès par l\'IA !')
    } else {
      const errorData = await res.json().catch(() => ({}))
      alert(`Erreur lors de la modification : ${errorData.message || 'Erreur inconnue'}`)
    }
  } catch (error) {
    console.error('Erreur:', error)
    alert('Une erreur s\'est produite lors de la modification')
  } finally {
    isModifying.value = false
  }
}

function cancelEdit() {
  validationErrors.value = []
  showErrors.value = false
  modificationDescription.value = ''
  mode.value = 'view'
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

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  border-width: 0.15rem;
}

textarea {
  resize: vertical;
}
</style>