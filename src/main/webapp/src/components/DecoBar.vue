<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-info navbar-fixed decorative-bar">
    <div class="container-fluid">
      <span class="navbar-brand mb-0 h1"></span>
    </div>
  </nav>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "DecorativeBar",
});
</script>

<style scoped>
.navbar-fixed {
  width: 100%;
  position: relative;
  margin: 0;
}

.navbar {
  background-color: #17a2b8 !important;
}

.navbar .container-fluid {
  max-width: 100%;
  padding-left: 1rem;
  padding-right: 1rem;
  margin: 0 auto;
}

.decorative-bar {
  pointer-events: none;
}
</style>