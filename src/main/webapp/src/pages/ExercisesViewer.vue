<template>
  <div class="container">
    <h2>Mes Exercices</h2>

    <div v-if="loading">Loading exercises...</div>
    <ul v-else>
      <li v-for="exercise in exercises" :key="exercise.id">
        {{ exercise.description }}
        <button class="delete" @click="deleteExercise(exercise.id)">🗑️ Delete</button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Exercise {
  id: number
  description: string
}

const exercises = ref<Exercise[]>([])
const loading = ref(false)

async function fetchExercises() {
  try {
    loading.value = true
    const res = await fetch('/api/teacher/generate/exercises')
    exercises.value = res.ok ? await res.json() : []
  } finally {
    loading.value = false
  }
}

async function deleteExercise(id: number) {
  await fetch(`/api/teacher/generate/exercises/${id}`, { method: "DELETE" })
  exercises.value = exercises.value.filter(e => e.id !== id)
}
onMounted(fetchExercises)
</script>