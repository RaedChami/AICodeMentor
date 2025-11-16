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
        <form @submit.prevent="updateExercise">
            <div v-if="validationErrors.length > 0" class="error-message">
              <p><strong>⚠️ Veuillez corriger les erreurs suivantes :</strong></p>
              <ul>
                <li v-for="(error, index) in validationErrors" :key="index">{{ error }}</li>
              </ul>
            </div>

            <p><strong>Description :</strong></p>
            <input
              v-model="exercise.description"
              type="text"
              required
              :class="{ 'input-error': !exercise.description?.trim() && showErrors }"
            />

            <p><strong>Difficulté :</strong></p>
            <input
              v-model="exercise.difficulty"
              type="text"
              required
              :class="{ 'input-error': !exercise.difficulty?.trim() && showErrors }"
            />

            <div>
              <label>Concepts :</label>
              <div class="concept-list">
                <div v-for="(_concept, index) in exercise.concepts" :key="index" class="concept-item">
                  <input
                    v-model="exercise.concepts[index]"
                    type="text"
                    required
                    placeholder="Nom du concept"
                    :class="{ 'input-error': !exercise.concepts[index]?.trim() && showErrors }"
                  />
                  <button type="button" @click="removeConcept(index)">Supprimer</button>
                </div>
              </div>
              <button type="button" @click="addConcept">Ajouter un concept</button>
            </div>

            <p><strong>Signature & Corps :</strong></p>
            <textarea
              v-model="exercise.signatureAndBody"
              required
              rows="25"
              :class="{ 'input-error': !exercise.signatureAndBody?.trim() && showErrors }"
            ></textarea>

            <p><strong>Tests JUnit :</strong></p>
            <textarea
              v-model="exercise.unitTests"
              required
              rows="25"
              :class="{ 'input-error': !exercise.unitTests?.trim() && showErrors }"
            ></textarea>

            <p><strong>Solution :</strong></p>
            <textarea
              v-model="exercise.solution"
              required
              rows="25"
              :class="{ 'input-error': !exercise.solution?.trim() && showErrors }"
            ></textarea>

            <button type="submit">Enregistrer</button>
            <button type="button" @click="cancelEdit">Annuler</button>
        </form>
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

function cancelEdit() {
  validationErrors.value = []
  showErrors.value = false
  mode.value = 'view'
  fetchExercise() // Recharger les données originales
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

.error-message {
  background-color: #fee;
  border: 2px solid #c33;
  border-radius: 5px;
  padding: 15px;
  margin-bottom: 20px;
}

.error-message p {
  color: #c33;
  margin: 0 0 10px 0;
}

.error-message ul {
  margin: 0;
  padding-left: 20px;
}

.error-message li {
  color: #c33;
  margin-bottom: 5px;
}

.input-error {
  border: 2px solid #c33 !important;
  background-color: #fff5f5;
}

input:focus.input-error,
textarea:focus.input-error {
  outline-color: #c33;
}
</style>