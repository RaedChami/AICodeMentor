<template>
  <div class="container">
    <div v-if="mode === 'form'">
      <h2>Créer un exercice</h2>

      <form @submit.prevent="createExercise">
        <input
          v-model="description"
          type="text"
          placeholder="Description de l'exercice"
          required
        />
        <button type="submit">Générer</button>
      </form>
    </div>

    <div v-if="mode === 'loading'">
      <h3>Génération de l'exercice...</h3>
      <p>Veuillez patienter, cela peut prendre quelques minutes.</p>
    </div>

    <div v-if="mode === 'result'">
      <h2>Exercice généré</h2>

      <div class="field">
        <strong>Description :</strong>
        <pre>{{ generatedExercise.description }}</pre>
      </div>

      <div class="field" v-if="generatedExercise.difficulty">
        <strong>Difficulté :</strong>
        <pre>{{ generatedExercise.difficulty }}</pre>
      </div>

      <div class="field" v-if="generatedExercise.concepts?.length">
        <strong>Concepts :</strong>
        <ul>
          <li v-for="c in generatedExercise.concepts" :key="c">{{ c }}</li>
        </ul>
      </div>

      <div class="field" v-if="generatedExercise.signatureAndBody">
        <strong>Signature & Corps :</strong>
        <pre>{{ generatedExercise.signatureAndBody }}</pre>
      </div>

      <div class="field" v-if="generatedExercise.unitTests">
        <strong>Unit Tests :</strong>
        <pre>{{ generatedExercise.unitTests }}</pre>
      </div>

      <div class="field" v-if="generatedExercise.solution">
        <strong>Solution :</strong>
        <pre>{{ generatedExercise.solution }}</pre>
      </div>
      <button @click="saveExercise">Valider</button>
      <button @click="reset">Créer un autre exercice</button>
      <button @click="revise">Réviser votre prompt initial</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const description = ref('')
const generatedExercise = ref<any>(null)
const mode = ref<'form' | 'loading' | 'result'>('form')

async function createExercise() {
  mode.value = 'loading'

  const res = await fetch('/api/teacher/generate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
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
    headers: { 'Content-Type': 'application/json' },
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

<style>
.container {
  max-width: 600px;
  margin: auto;
}
pre {
  background: #eee;
  padding: 1rem;
  border-radius: 8px;
}
</style>
