<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary w-100">
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
        { label: "Accueil", path: "/" },
        { label: "Mes Exercices", path: "/teacher/exercises" },
      ],
    };
  },
});
</script>