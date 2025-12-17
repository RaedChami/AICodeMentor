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
        <ul class="navbar-nav align-items-center">
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

          <li
            v-if="user"
            class="nav-item d-flex align-items-center gap-2 text-white fw-semibold ms-3"
          >
            <span class="navbar-text text-white">
              Bonjour {{ user.name }} {{ user.lastName }}
            </span>

            <button
              class="btn btn-outline-light btn-sm"
              @click="logout"
            >
              Déconnexion
            </button>
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
        { label: "Tous les Exercices", path: "/teacher/exercises" },
        { label: "Mes Exercices", path: "/teacher/my-exercises" },
      ],
      user: JSON.parse(sessionStorage.getItem("user") || "null"),
    };
  },
  methods: {
    logout() {
      sessionStorage.removeItem("user");
      this.$router.push("/");
    },
  },
});
</script>
