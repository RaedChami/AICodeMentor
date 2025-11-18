<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-info navbar-fixed">
    <div class="container-fluid">
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
        aria-controls="navbarNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li
            v-for="item in navItems"
            :key="item.path"
            class="nav-item"
          >
            <router-link
              :to="item.path"
              class="nav-link"
              active-class="active"
            >
              {{ item.label }}
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import { defineComponent } from "vue";

interface NavItem {
  label: string;
  path: string;
}

export default defineComponent({
  name: "NavBar",
  data(): { navItems: NavItem[] } {
    return {
      navItems: [
        { label: "Accueil", path: "/teacher/generate" },
        { label: "Mes Exercices", path: "/teacher/exercises" },
      ],
    };
  },
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

.nav-link {
  color: rgba(255, 255, 255, 0.85) !important;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #ffffff !important;
}

.nav-link.active {
  color: #ffffff !important;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}
</style>