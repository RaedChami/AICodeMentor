<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-3 text-light">
        <i class="bi bi-funnel"></i> Filtrer par difficulté
      </h5>
      <div class="d-flex flex-wrap gap-2">
        <button
          class="btn"
          :class="modelValue === null ? 'btn-primary' : 'btn-outline-primary'"
          @click="$emit('update:modelValue', null)"
        >
          Tous ({{ totalCount }})
        </button>
        <button
          v-for="level in difficultyLevels"
          :key="level"
          class="btn"
          :class="modelValue === level ? 'btn-primary' : 'btn-outline-primary'"
          @click="$emit('update:modelValue', level)"
        >
          {{ level }} ({{ counts[level] || 0 }})
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: string | null
  items: Array<{ difficulty: string }>
  difficultyLevels?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  difficultyLevels: () => ['L1', 'L2', 'L3', 'M1', 'M2']
})

defineEmits<{
  'update:modelValue': [value: string | null]
}>()

const totalCount = computed(() => props.items.length)

const counts = computed(() => {
  const result: Record<string, number> = {}
  props.difficultyLevels.forEach(level => {
    result[level] = props.items.filter(item => item.difficulty === level).length
  })
  return result
})
</script>

<style scoped>
.gap-2 {
  gap: 0.5rem;
}
</style>