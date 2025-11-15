<template>
  <div class="container">
    <h2>Exercice {{ exercise?.id }}</h2>

    <div v-if="loading">Chargement…</div>

    <div v-else-if="exercise">
      <div v-if="mode === 'view'">
        <p><strong>Description :</strong></p>
        <p>{{ exercise.description }}</p>

        <p><strong>Difficulté :</strong></p>
        <pre>{{ exercise.difficulty }}</pre>

        <p><strong>Concepts :</strong></p>
        <ul>
          <li v-for="(concept, index) in exercise.concepts" :key="index">{{ concept }}</li>
        </ul>

        <p><strong>Signature & Corps :</strong></p>
        <pre>{{ exercise.signatureAndBody }}</pre>

        <p><strong>Unit Tests :</strong></p>
        <pre>{{ exercise.unitTests }}</pre>

        <p><strong>Solution :</strong></p>
        <pre>{{ exercise.solution }}</pre>

        <button @click="goBack">Retour</button>
        <button @click="mode = 'modify'">Modifier</button>
        <button class="delete" @click="deleteExercise(exercise.id)">Supprimer</button>
      </div>

      <div v-else-if="mode === 'modify'">
        <p><strong>Description :</strong></p>
        <input v-model="exercise.description" type="text" />

        <p><strong>Difficulté :</strong></p>
        <input v-model="exercise.difficulty" type="text" />

        <div>
          <label>Concepts :</label>
          <div class="concept-list">
            <div v-for="(_concept, index) in exercise.concepts" :key="index" class="concept-item">
              <input v-model="exercise.concepts[index]" type="text" placeholder="Nom du concept" />
              <button @click="removeConcept(index)">Supprimer</button>
            </div>
          </div>
          <button @click="addConcept">Ajouter un concept</button>
        </div>

        <p><strong>Signature & Corps :</strong></p>
        <textarea v-model="exercise.signatureAndBody" rows="5"></textarea>

        <p><strong>Tests JUnit :</strong></p>
        <textarea v-model="exercise.unitTests" rows="5"></textarea>

        <p><strong>Solution :</strong></p>
        <textarea v-model="exercise.solution" rows="5"></textarea>

        <button @click="saveExercise()">Enregistrer</button>
        <button @click="mode='view'">Annuler</button>
      </div>
    </div>

    <div v-else>
      <p>Exercice introuvable.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const exercise = ref<any>(null)
const loading = ref(true)
const mode = ref<'view' | 'modify'>('view')

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
  const res = await fetch(`/api/teacher/exercises/${id}`, { method: "DELETE" })
  if (res.ok) {
    router.push('/teacher/exercises')
  } else {
    alert('Impossible de supprimer')
  }
}

async function saveExercise() {
  const res = await fetch(`/api/teacher/generate/save`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(exercise.value)
  })

  if (res.ok) {
    const updated = await res.json()
    exercise.value = updated
    mode.value = 'view'
    alert('Exercice mis à jour')
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

function goBack() {
  router.back()
}

onMounted(fetchExercise)
</script>

<style scoped>
.concept-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.concept-item input {
  flex: 1;
}
</style>