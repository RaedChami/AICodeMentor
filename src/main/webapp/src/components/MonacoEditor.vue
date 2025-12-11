<template>
  <div ref="editorContainer" class="monaco-container"></div>
</template>

<script setup lang="ts">
import * as monaco from "monaco-editor"
import { onMounted, onBeforeUnmount, ref, watch } from "vue"

const props = defineProps<{
  modelValue: string
  language?: string
  readOnly?: boolean
}>()

const emit = defineEmits(["update:modelValue"])

const editorContainer = ref<HTMLElement | null>(null)
let editor: monaco.editor.IStandaloneCodeEditor | null = null

onMounted(() => {
  editor = monaco.editor.create(editorContainer.value!, {
    value: props.modelValue,
    language: "java",
    theme: "vs-dark",
    automaticLayout: true,
    readOnly: props.readOnly ?? false,
    minimap: { enabled: false },
  })

  editor.onDidChangeModelContent(() => {
    emit("update:modelValue", editor!.getValue())
  })
})

watch(() => props.modelValue, (newValue) => {
  if (editor && editor.getValue() !== newValue) {
    editor.setValue(newValue)
  }
})

onBeforeUnmount(() => {
  editor?.dispose()
})
</script>

<style scoped>
.monaco-container {
  width: 100%;
  height: 400px;
  border: 1px solid #444;
}
</style>
