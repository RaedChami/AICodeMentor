<template>
  <Navbar v-if="mode !== 'loading'" />
    <div class="page-wrapper">
      <div v-if="mode === 'form'" class="content-container">
        <h1>Créer mon exercice en un seul clic</h1>

        <form @submit.prevent="createExercise" class="exercise-form">
          <input
            id="description"
            v-model="description"
            type="text"
            class="form-input"
            placeholder="Demandez un exercice"
            required
          />
          <button type="submit" class="btn btn-info w-25 mx-auto">
            <strong>Générer</strong>
          </button>
        </form>
      </div>

      <div v-if="mode === 'loading'" class="content-container text-center">
        <div class="spinner-border text-info mb-4" style="width: 4rem; height: 4rem;" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
        <h3 class="h4 mb-3">Génération de l'exercice...</h3>
        <p class="text-muted">Veuillez patienter, cela peut prendre quelques minutes.</p>
      </div>

      <div v-if="mode === 'result'" class="content-container">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
          <h1 style="font-size: 2em; margin-bottom: 0;">Exercice généré</h1>
          <div class="d-flex gap-2 flex-wrap">
            <button @click="revise" class="btn-submit" style="background-color: #6c757d;">
              Réviser le prompt
            </button>
            <button @click="reset" class="btn-submit">
              Créer un autre
            </button>
            <button @click="saveExercise" class="btn-submit" style="background-color: #28a745;">
              Valider et sauvegarder
            </button>
          </div>
        </div>

        <div class="row g-4">
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

          <div class="col-lg-8">
            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.signatureAndBody">
              <div class="card-header bg-success text-white py-3">
                <h5 class="mb-0">Signature & Corps</h5>
              </div>
              <div class="card-body p-4">
                <pre class="code-block"><code>{{ generatedExercise.signatureAndBody }}</code></pre>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.unitTests">
              <div class="card-header bg-info text-white py-3">
                <h5 class="mb-0">Tests Unitaires</h5>
              </div>
              <div class="card-body p-4">
                <pre class="code-block"><code>{{ generatedExercise.unitTests }}</code></pre>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4" v-if="generatedExercise.solution">
              <div class="card-header bg-warning text-dark py-3">
                <h5 class="mb-0">Solution</h5>
              </div>
              <div class="card-body p-4">
                <pre class="code-block"><code>{{ generatedExercise.solution }}</code></pre>
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
/* Style de base pour la page - Thème sombre */
.page-wrapper {
  font-family: 'Arial', sans-serif;
  background-color: #1e1e2f;
  color: #ffffff;
  min-height: calc(100vh - 56px);
  padding: 20px;
}

/* Conteneur principal */
.content-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #2a2a3b;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
  border-radius: 8px;
}

/* Mode résultat - conteneur plus large */
.mode-result .content-container {
  max-width: 1400px;
}

/* Style pour le grand titre */
h1 {
  font-size: 2.5em;
  color: #ffffff;
  text-align: center;
  margin-bottom: 20px;
}

/* Styles pour le formulaire */
.exercise-form {
  display: flex;
  flex-direction: column;
}

/* Style pour les champs de saisie */
.form-input {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #444;
  border-radius: 4px;
  font-size: 1em;
  width: 100%;
  box-sizing: border-box;
  color: #ffffff;
  background-color: #3a3a4e;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  border-color: #007bff;
  outline: none;
}

/* Style pour les boutons */
.btn-submit {
  background-color: #007bff;
  color: white;
  font-size: 1em;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-submit:hover {
  background-color: #0056b3;
}

/* Styles pour les blocs de code */
.code-block {
  margin: 0;
  padding: 15px;
  background-color: #3a3a4e;
  border-radius: 4px;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.95rem;
  line-height: 1.6;
  border: 1px solid #444;
}

code {
  color: #ffffff;
}

.card {
  border: none;
  background-color: #2a2a3b;
}

.card-body {
  background-color: #2a2a3b;
  color: #ffffff;
}

.card-header h5 {
  font-size: 1.1rem;
  font-weight: 600;
}

.badge {
  font-size: 0.9rem;
  font-weight: 500;
}

.text-muted {
  color: #b0b0c0 !important;
}

/* Responsivité */
@media (max-width: 600px) {
  h1 {
    font-size: 2em;
  }

  .content-container {
    padding: 15px;
  }

  .btn-submit {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>