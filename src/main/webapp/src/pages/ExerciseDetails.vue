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
        <pre>{{ exercise.concepts }}</pre>

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

        <p><strong>Concepts (séparés par des virgules) :</strong></p>
        <input v-model="conceptsInput" type="text" />

        <p><strong>Signature & Corps :</strong></p>
        <textarea v-model="exercise.signatureAndBody" rows="5"></textarea>

        <p><strong>Unit Tests :</strong></p>
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
const conceptsInput = ref('')

async function fetchExercise() {
  try {
    loading.value = true
    const id = route.params.id
    const res = await fetch(`/api/teacher/generate/exercises/${id}`)
    if (res.ok) {
      const data = await res.json()
      exercise.value = data
      conceptsInput.value = data.concepts?.join(', ') || ''
    } else {
      exercise.value = null
    }
  } finally {
    loading.value = false
  }
}

async function deleteExercise(id: number) {
  const res = await fetch(`/api/teacher/generate/exercises/${id}`, { method: "DELETE" })
  if (res.ok) {
    router.push(`/teacher/generate/exercises`)
  } else {
    alert('Impossible de supprimer')
  }
}

async function saveExercise() {
  exercise.value.concepts = conceptsInput.value.split(',').map((c: string) => c.trim())

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

function goBack() {
  router.back()
}

onMounted(fetchExercise)
</script>
