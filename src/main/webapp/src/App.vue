<template>
  <div id="app">
    <!-- Barre de navigation -->
    <nav class="navbar">
      <router-link to="/teacher/generate" class="nav-link">
      Accueil
      </router-link>
      <router-link to="/teacher/generate/exercises" class="nav-link">
      Consulter mes exercice
      </router-link>
    </nav>

    <!-- Zone où les pages s’affichent -->
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style>
body {
  margin: 0;
  font-family: Arial, sans-serif;
}

.navbar {
  background: #2b66ff;
  padding: 1rem;
  display: flex;
  gap: 1rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: bold;
}

.nav-link:hover {
  text-decoration: underline;
}

.content {
  padding: 2rem;
}
</style>
