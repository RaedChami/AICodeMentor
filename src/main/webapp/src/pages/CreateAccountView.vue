<template>
    <DecoBar />
    <div>
        <div class="container mt-5">
        <h2>Créer un compte</h2>

        <form @submit.prevent="createAccount" class="mt-3">

          <input
            v-model="name"
            class="form-control mb-2"
        placeholder="Prenom"
      />

      <input
        v-model="lastName"
        class="form-control mb-3"
        placeholder="Nom"
      />

          <div class="mb-3">
            <label class="d-block fw-bold mb-2">Vous êtes :</label>

            <div class="form-check">
              <input
                class="form-check-input"
                type="radio"
                value="Teacher"
                v-model="role"
                id="roleTeacher"
              />
              <label class="form-check-label" for="roleTeacher">
                Teacher
              </label>
            </div>

            <div class="form-check">
              <input
                class="form-check-input"
                type="radio"
                value="Student"
                v-model="role"
                id="roleStudent"
              />
              <label class="form-check-label" for="roleStudent">
                Student
              </label>
            </div>

            <div v-if="roleError" class="text-danger mt-2">
              Vous devez choisir un rôle.
            </div>
          </div>

          <button type="submit" class="btn btn-info">
            Créer le compte
          </button>
        </form>

      </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import DecoBar from '../components/DecoBar.vue'

const router = useRouter()

const name = ref("")
const lastName = ref("")
const role = ref("")
const roleError = ref(false)
const errorMessage = ref("")
async function createAccount() {
  roleError.value = false
  errorMessage.value = ""
  if (!name.value.trim() || !lastName.value.trim()) return
  if (!role.value) {
    roleError.value = true
    return
  }

  const user = {
    name: name.value,
    lastName: lastName.value,
    role: role.value
  }

  const res = await fetch("/api/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user)
  })
  const data = await res.json()

      if (!data || !data.role) {
        errorMessage.value = "Compte invalide."
        return
      }

      if (data.role === "Teacher") {
        router.push("/teacher")
      } else {
        router.push("/student")
      }
}
</script>

