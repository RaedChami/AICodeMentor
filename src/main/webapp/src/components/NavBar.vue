<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary w-100">
    <div class="container-fluid">

      <!-- Bouton responsive -->
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

      <div class="collapse navbar-collapse justify-content-between" id="navbarNav">

        <!-- Liens de navigation -->
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <a class="nav-link" @click="goHome">Accueil</a>
          </li>

          <li class="nav-item">
            <router-link to="/login" class="nav-link">Se connecter</router-link>
          </li>
        </ul>

        <!-- 🔥 Affichage du nom -->
        <span v-if="user" class="navbar-text text-white fw-bold">
          Bonjour, {{ user.name }} {{ user.lastName }}
        </span>

      </div>
    </div>
  </nav>
</template>


<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "NavBar",

  setup() {
    const router = useRouter();
    const user = ref<{ name: string; lastName: string; role: string } | null>(null);

    onMounted(() => {
      const stored = localStorage.getItem("user");
      if (stored) {
        user.value = JSON.parse(stored);
      }
    });

    function goHome() {
      if (!user.value) {
        router.push("/");
        return;
      }
      if (user.value.role === "Teacher") {
        router.push("/teacher");
      } else {
        router.push("/student");
      }
    }

    return { goHome, user };
  }
});
</script>

