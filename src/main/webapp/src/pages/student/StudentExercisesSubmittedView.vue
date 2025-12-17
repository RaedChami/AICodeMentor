<template>
  <StudentNavBar />
  <div class="container-fluid py-4">
    <div class="row mb-4">
      <div class="col">
        <h2>Continuer vos Exercices</h2>
      </div>
    </div>

    <div class="row mb-4">
      <div class="col">
        <DifficultyFilter
          v-model="selectedDifficulty"
          :items="exercises"
        />
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-3 text-muted">Chargement des exercices...</p>
    </div>

    <div v-else-if="exercises.length === 0" class="alert alert-info d-flex justify-content-center w-50 mx-auto bg-warning" role="alert">
      <div class="text-dark"><strong>Vous n'avez essayé aucun Exercice pour l'instant !</strong></div>
    </div>

    <div v-else-if="filteredExercises.length === 0" class="alert alert-info" role="alert">
      <strong>Aucun exercice</strong> ne correspond au niveau {{ selectedDifficulty }}.
    </div>

    <div v-else class="row g-3">
      <div
        v-for="exercise in filteredExercises"
        :key="exercise.id"
        class="col-12 col-md-6 col-lg-4 col-xl-3"
      >
        <div
          class="card h-100 exercise-card shadow-sm"
          @click="openExercise(exercise.id)"
          role="button"
          tabindex="0"
          @keypress.enter="openExercise(exercise.id)"
        >
          <div class="card-body d-flex flex-column">
            <div class="d-flex align-items-start mb-3">
              <div class="flex-grow-1">
                <span class="badge bg-info mb-2">Exercice #{{ exercise.id }}</span>
                <span
                  class="badge ms-2 mb-2"
                  :class="getDifficultyBadgeClass(exercise.difficulty)"
                >
                  {{ exercise.difficulty }}
                </span>
              </div>
            </div>
            <p class="card-text flex-grow-1 mb-3">
              {{ exercise.description }}
            </p>
            <div class="mt-auto">
              <button class="btn btn-outline-info btn-sm w-100">
                Consulter
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import StudentNavBar from '../../components/StudentNavBar.vue'
import DifficultyFilter from '../../components/DifficultyFilter.vue'

interface Exercise {
  id: number
  description: string
  difficulty: 'L1' | 'L2' | 'L3' | 'M1' | 'M2'
}

const exercises = ref<Exercise[]>([])
const loading = ref(false)
const selectedDifficulty = ref<string | null>(null)
const router = useRouter()

const filteredExercises = computed(() => {
  if (selectedDifficulty.value === null) {
    return exercises.value
  }
  return exercises.value.filter(ex => ex.difficulty === selectedDifficulty.value)
})

const user = JSON.parse(sessionStorage.getItem("user") || "{}")


function getDifficultyBadgeClass(difficulty: string): string {
  const classes: Record<string, string> = {
    'L1': 'bg-success',
    'L2': 'bg-primary',
    'L3': 'bg-info',
    'M1': 'bg-warning text-dark',
    'M2': 'bg-danger'
  }
  return classes[difficulty] || 'bg-secondary'
}

async function fetchExercises() {
  try {
    loading.value = true

    if (!user.id) {
      console.error("Utilisateur non connecté")
      exercises.value = []
      return
    }

    const res = await fetch(
      `/api/student/exercises-submitted?loginId=${user.id}`,
      { method: "GET" }
    )

    exercises.value = res.ok ? await res.json() : []
  } finally {
    loading.value = false
  }
}


function openExercise(id: number) {
  router.push(`/student/exercises-submitted/${id}`)
}

onMounted(fetchExercises)
</script>

<style scoped>
.exercise-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.exercise-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.exercise-card:active {
  transform: translateY(-2px);
}

.card-text {
  color: #6c757d;
  font-size: 0.95rem;
  line-height: 1.5;
}

.gap-2 {
  gap: 0.5rem;
}
</style>