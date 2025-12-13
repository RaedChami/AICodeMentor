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
        <!-- Liens à gauche -->
        <ul class="navbar-nav">
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

        <span
          v-if="user"
          class="navbar-text ms-auto text-white fw-semibold"
        >
          Bonjour {{ user.name }} {{ user.lastName }}
        </span>
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

interface User {
  id: number;
  name: string;
  lastName: string;
  role: string;
}

export default defineComponent({
  name: "NavBar",
  data(): { navItems: NavItem[]; user: User | null } {
    return {
      navItems: [
        { label: "Accueil", path: "/teacher/generate" },
        { label: "Mes Exercices", path: "/teacher/exercises" },
      ],
      user: JSON.parse(localStorage.getItem("user") || "null"),
    };
  },
});
</script>
