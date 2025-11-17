<template>
  <div>
    <Navbar />
    <div class="container-fluid">
      <div v-if="mode === 'form'" class="row justify-content-center">
        <div class="col-lg-100 col-xl-200">

          <div class="card shadow-sm border-0">
            <div class="card-body p-5">
              <h2 class="h4 mb-4">Créer un exercice</h2>

              <form @submit.prevent="createExercise">
                <div class="mb-4">
                  <input
                    id="description"
                    v-model="description"
                    type="text"
                    class="form-control form-control-lg"
                    placeholder="Demandez un exercice"
                    required
                  />
                </div>
                <button type="submit" class="btn btn-info border px-4 py-2">
                  <strong>Générer</strong>
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>

      <div v-if="mode === 'loading'" class="row justify-content-center">
        <div class="col-lg-6 text-center py-5">
          <div class="spinner-border text-primary mb-4" style="width: 4rem; height: 4rem;" role="status">
            <span class="visually-hidden">Chargement...</span>
          </div>
          <h3 class="h4 mb-3">Génération de l'exercice...</h3>
          <p class="text-muted">Veuillez patienter, cela peut prendre quelques minutes.</p>
        </div>
      </div>

      <div v-if="mode === 'result'">
        <div class="d-flex justify-content-between align-items-center mb-5">
          <h2 class="h3 mb-0">Exercice généré</h2>
          <div class="d-flex gap-3">
            <button @click="revise" class="btn btn-outline-secondary px-4">
              Réviser le prompt
            </button>
            <button @click="reset" class="btn btn-outline-primary px-4">
              Créer un autre
            </button>
            <button @click="saveExercise" class="btn btn-success px-4">
              Valider et sauvegarder
            </button>
          </div>
        </div>

        <div class="row g-4">
          <!-- Colonne gauche: Métadonnées -->
          <div class="col-lg-4">
            <div class="card shadow-sm border-0 mb-4">
              <div class="card-header bg-primary text-white py-3">
                <h5 class="mb-0">Caractéristiques</h5>
              </div>
              <div class="card-body p-4">
                <div class="mb-4">
                  <label class="form-label fw-bold mb-2">Description :</label>
                  <p class="text-muted mb-0">{{ generatedExercise.description }}</p>
                </div>

                <div class="mb-4" v-if="generatedExercise.difficulty">
                  <label class="form-label fw-bold mb-2">Difficulté :</label>
                  <p class="text-muted mb-0">{{ generatedExercise.difficulty }}</p>
                </div>

                <div v-if="generatedExercise.concepts?.length">
                  <label class="form-label fw-bold mb-3">Concepts :</label>
                  <div class="d-flex flex-wrap gap-2">
                    <span
                      v-for="c in generatedExercise.concepts"
                      :key="c"
                      class="badge bg-secondary py-2 px-3"
                    >
                      {{ c }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Colonne droite: Code -->
          <div class="col-lg-8">
            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.signatureAndBody">
              <div class="card-header bg-success text-white py-3">
                <h5 class="mb-0">Signature & Corps</h5>
              </div>
              <div class="card-body p-4">
                <pre class="bg-light p-4 rounded mb-0"><code>{{ generatedExercise.signatureAndBody }}</code></pre>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.unitTests">
              <div class="card-header bg-info text-white py-3">
                <h5 class="mb-0">Tests Unitaires</h5>
              </div>
              <div class="card-body p-4">
                <pre class="bg-light p-4 rounded mb-0"><code>{{ generatedExercise.unitTests }}</code></pre>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.solution">
              <div class="card-header bg-warning text-dark py-3">
                <h5 class="mb-0">Solution</h5>
              </div>
              <div class="card-body p-4">
                <pre class="bg-light p-4 rounded mb-0"><code>{{ generatedExercise.solution }}</code></pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../components/NavBar.vue'

const description = ref('')
const generatedExercise = ref<any>(null)
const mode = ref<'form' | 'loading' | 'result'>('form')

async function createExercise() {
  mode.value = 'loading'

  const res = await fetch('/api/teacher/generate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
    body: JSON.stringify({ prompt: description.value })
  })

  if (!res.ok) {
    mode.value = 'form'
    alert('Erreur dans la génération')
    return
  }

  const data = await res.json()
  generatedExercise.value = data
  mode.value = 'result'
}

async function saveExercise() {
  if (!generatedExercise.value) return;

  const res = await fetch('/api/teacher/generate/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
    body: JSON.stringify(generatedExercise.value)
  });

  if (!res.ok) {
    alert('Erreur lors de la sauvegarde');
    return;
  }

  alert('Exercice enregistré avec succès !');
  reset();
}

function reset() {
  description.value = ''
  generatedExercise.value = null
  mode.value = 'form'
}

function revise() {
  generatedExercise.value = null
  mode.value = 'form'
}
</script>

<style scoped>
pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.95rem;
  line-height: 1.6;
}

code {
  color: #212529;
}

.card {
  border: none;
}

.card-header h5 {
  font-size: 1.1rem;
  font-weight: 600;
}

.badge {
  font-size: 0.9rem;
  font-weight: 500;
}
</style>