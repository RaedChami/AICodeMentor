<template>
  <div class="container">
    <h2>Mes Exercices</h2>

    <div v-if="loading">Loading exercises...</div>
    <ul v-else>
      <li v-for="exercise in exercises" :key="exercise.id" @click="openExercise(exercise.id)" class="exercise-item">
        {{ exercise.id }} - {{ exercise.description }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

interface Exercise {
  id: number
  description: string
}

const exercises = ref<Exercise[]>([])
const loading = ref(false)
const router = useRouter()

async function fetchExercises() {
  try {
    loading.value = true
    const res = await fetch('/api/teacher/exercises')
    exercises.value = res.ok ? await res.json() : []
  } finally {
    loading.value = false
  }
}

function openExercise(id: number) {
  router.push(`/teacher/exercises/${id}`)
}

onMounted(fetchExercises)
</script>