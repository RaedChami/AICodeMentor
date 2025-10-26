<template>
  <div class="container">
    <h2>Create Exercise</h2>

    <!-- Formulaire de création -->
    <form @submit.prevent="createExercise">
      <input
          v-model="description"
          type="text"
          placeholder="Enter exercise description"
          required
      />
      <button type="submit">Create</button>
    </form>

    <!-- Message -->
    <p v-if="message" class="message">{{ message }}</p>

    <hr />

    <!-- Liste des exercices -->
    <h3>Existing Exercises</h3>
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

const description = ref<string>('')
const exercises = ref<Exercise[]>([])
const message = ref<string>('')
const loading = ref<boolean>(false)

async function fetchExercises() {
  try {
    loading.value = true
    const res = await fetch('/api/exercises')
    if (res.ok) {
      exercises.value = await res.json()
    } else {
      console.error('Failed to fetch exercises:', res.status)
    }
  } catch (err) {
    console.error('Error fetching exercises:', err)
  } finally {
    loading.value = false
  }
}

async function createExercise() {
  try {
    const res = await fetch('/api/exercises', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description: description.value }),
    })
    if (res.ok) {
      message.value = 'Exercise created successfully!'
      description.value = ''
      await fetchExercises()
    } else {
      message.value = 'Failed to create exercise.'
    }
  } catch (err) {
    console.error(err)
    message.value = 'Error while creating exercise.'
  } finally {
    setTimeout(() => (message.value = ''), 2000)
  }
}

async function deleteExercise(id: number) {
  if (!confirm('Are you sure you want to delete this exercise?')) return
  try {
    const res = await fetch(`/api/exercises/${id}`, { method: 'DELETE' })
    if (res.ok) {
      exercises.value = exercises.value.filter(e => e.id !== id)
      message.value = 'Exercise deleted!'
    } else {
      message.value = 'Failed to delete exercise.'
    }
  } catch (err) {
    console.error(err)
    message.value = 'Error while deleting exercise.'
  } finally {
    setTimeout(() => (message.value = ''), 2000)
  }
}

onMounted(fetchExercises)
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 2rem;
}
.container {
  max-width: 600px;
  margin: auto;
}
input {
  padding: 0.5rem;
  width: 70%;
  margin-right: 0.5rem;
}
button {
  padding: 0.5rem 1rem;
}
button.delete {
  margin-left: 1rem;
  background-color: #ff4d4d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button.delete:hover {
  background-color: #ff1a1a;
}
.message {
  color: green;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  background: #f9f9f9;
  border: 1px solid #ddd;
  margin: 0.25rem 0;
  padding: 0.5rem;
  border-radius: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
