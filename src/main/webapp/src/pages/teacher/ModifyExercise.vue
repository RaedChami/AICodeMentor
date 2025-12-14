<template>
  <DecoBar />
  <div class="container-fluid py-4">
    <div class="row mb-4">
      <div class="col">
        <h2 class="mb-0">Exercice {{ exercise?.id }}</h2>
      </div>
    </div>

    <div v-if="mode === 'loading'" class="content-container text-center">
      <div class="spinner-border text-info mb-4" style="width: 4rem; height: 4rem;" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <h3 class="h4 mb-3">{{ isModifying ? 'Modification de l\'exercice en cours...' : 'Génération de l\'exercice...' }}</h3>
    </div>

    <div v-else-if="exercise">
      <div v-if="mode === 'view'">
        <p class="text-light mt-2">
          <i class="bi bi-person-fill me-1"></i>
          Créé par : {{ exercise.creator.name }} {{ exercise.creator.lastName }}
        </p>
        <div class="d-flex justify-content-end gap-2 mb-3">
          <button @click="goBack" class="btn btn-primary me-auto bg-primary">
            Retour
          </button>
          <button
            @click="mode = 'modify'"
            class="btn btn-warning"
            :disabled="!isOwner"
            :title="!isOwner ? 'Vous ne pouvez modifier que vos propres exercices' : ''"
          >
            Modifier avec IA
          </button>

          <button
            class="btn btn-danger"
            @click="deleteExercise(exercise.id)"
            :disabled="!isOwner"
          >
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
                  <MonacoEditor
                    :model-value="exercise.signatureAndBody"
                    language="java"
                    height="450px"
                    :read-only="true"
                    theme="vs-dark"
                  />
                </div>
              </div>

              <div class="card shadow-sm mb-3">
                <div class="card-header bg-info text-white">
                  <h5 class="mb-0">Tests JUnit</h5>
                </div>
                <div class="card-body">
                  <MonacoEditor
                    :model-value="exercise.unitTests"
                    language="java"
                    height="450px"
                    :read-only="true"
                    theme="vs-dark"
                  />
                </div>
              </div>

              <div class="card shadow-sm">
                <div class="card-header bg-warning text-dark">
                  <h5 class="mb-0">Solution</h5>
                </div>
                <div class="card-body">
                  <MonacoEditor
                    :model-value="exercise.solution"
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
                      <MonacoEditor
                        :model-value="exercise.signatureAndBody"
                        language="java"
                        height="450px"
                        :read-only="true"
                        theme="vs-dark"
                      />
                      <p><strong>Tests Unitaires:</strong></p>
                      <MonacoEditor
                        :model-value="exercise.unitTests"
                        language="java"
                        height="450px"
                        :read-only="true"
                        theme="vs-dark"
                      />
                      <p><strong>Solution:</strong></p>
                      <MonacoEditor
                        :model-value="exercise.solution"
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
          </div>

          <div class="row mt-4">
            <div class="col">
              <div class="d-flex gap-2 justify-content-end">
                <button type="button" class="btn btn-secondary" @click="cancelEdit">
                  Annuler
                </button>
                <button type="submit" class="btn btn-success">
                  <i class="bi bi-magic me-2"></i>
                  Générer avec IA
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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DecoBar from '../../components/DecoBar.vue'
import MonacoEditor from '../../components/MonacoEditor.vue'

const route = useRoute()
const router = useRouter()
const exercise = ref<any>(null)
const loading = ref(true)
const mode = ref<'view' | 'modify' | 'loading'>('view')
const validationErrors = ref<string[]>([])
const showErrors = ref(false)
const modificationDescription = ref('')
const isModifying = ref(false)
const currentUser = JSON.parse(localStorage.getItem("user") || "{}")

const isOwner = computed(() => {
  return exercise.value?.creator?.id === currentUser.id
})
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

  const res = await fetch(`/api/teacher/exercises/${id}?userId=${currentUser.id}`, { method: "DELETE" })
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
    mode.value = 'loading'

    const res = await fetch(`/api/teacher/exercises/${exercise.value.id}/modify`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prompt: modificationDescription.value,
        creatorId: currentUser.id
      })
    })

    if (res.ok) {
      const updated = await res.json()
      exercise.value = updated
      modificationDescription.value = ''
      validationErrors.value = []
      showErrors.value = false
      mode.value = 'view'
    } else {
      const errorData = await res.json().catch(() => ({}))
      if (res.status === 403) {
        alert(errorData.message || "Accès interdit")
        mode.value = 'view'
        return
      }

      alert(`Erreur lors de la modification : ${errorData.message || 'Erreur inconnue'}`)
      mode.value = 'modify'
    }
  } catch (error) {
    console.error('Erreur:', error)
    alert('Une erreur s\'est produite lors de la modification')
    mode.value = 'modify'
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

.content-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}
</style>