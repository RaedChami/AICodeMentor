<template>
    <div>
        <Navbar />
          <div class="container mt-5">
            <h2>Connexion</h2>

            <form @submit.prevent="login" class="mt-3">
              <input v-model="name" class="form-control mb-2" placeholder="Prénom" />
              <input v-model="lastName" class="form-control mb-3" placeholder="Nom" />

              <button type="submit" class="btn btn-primary">Se connecter</button>
              <button
                  type="button"
                  class="btn btn-link mt-3"
                  @click="router.push('/create-account')"
                >
                  Créer un compte
                </button>
            </form>

            <div v-if="errorMessage" class="text-danger mt-3">
              {{ errorMessage }}
            </div>
          </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import Navbar from '../components/NavBar.vue'

const router = useRouter()

const name = ref("")
const lastName = ref("")
const errorMessage = ref("")

async function login() {
  errorMessage.value = ""

  if (!name.value.trim() || !lastName.value.trim()) {
    errorMessage.value = "Veuillez remplir tous les champs."
    return
  }

  const user = { name: name.value, lastName: lastName.value }

  const res = await fetch("/api/login/check", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user)
  })

  if (!res.ok) {
    errorMessage.value = "Ce compte n'existe pas. Veuillez en créer un."
    return
  }


  const data = await res.json()
  localStorage.setItem("user", JSON.stringify(data))

    if (!data || !data.role) {
      errorMessage.value = "Compte invalide."
      return
    }

    // 🔥 Redirection selon le rôle
    if (data.role === "Teacher") {
      router.push("/teacher")
    } else {
      router.push("/student")
    }
}

</script>