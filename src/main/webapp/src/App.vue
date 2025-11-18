<template>
  <div id="app">
    <header class="app-header">
      <h1 class="main-title text-info">AICodeMentor</h1>
    </header>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>


<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  overflow-x: hidden;
}

body {
  font-family: 'Arial', sans-serif;
  margin: 0;
  padding: 0;
  background-color: #1e1e2f;
  color: #ffffff;
}

#app {
  min-height: 100vh;
  width: 100%;
  background-color: #1e1e2f;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.app-header {
  width: 100%;
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: #1e1e2f;
}

.main-title {
  font-size: 2.5em;
  color: #ffffff;
  text-align: center;
  padding: 30px 20px 20px 20px;
  margin: 0;
  background: linear-gradient(135deg, #2a2a3b 0%, #1e1e2f 100%);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
  width: 100%;
}

.content {
  flex: 1;
  width: 100%;
  padding: 0;
  background-color: #1e1e2f;
  overflow-x: hidden;
}

/* Styles globaux pour les éléments de formulaire */
input[type="text"],
input[type="email"],
input[type="password"],
textarea,
select {
  background-color: #3a3a4e;
  color: #ffffff;
  border: 1px solid #444;
  border-radius: 4px;
  padding: 10px;
  transition: border-color 0.3s ease;
}

input[type="text"]:focus,
input[type="email"]:focus,
input[type="password"]:focus,
textarea:focus,
select:focus {
  border-color: #007bff;
  outline: none;
}

input::placeholder,
textarea::placeholder {
  color: #b0b0c0;
}

button {
  background-color: #007bff;
  color: white;
  font-size: 1em;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

button:disabled {
  background-color: #444;
  cursor: not-allowed;
  opacity: 0.6;
}

.card {
  background-color: #2a2a3b;
  border: 1px solid #444;
  color: #ffffff;
}

.card-header {
  background-color: #3a3a4e;
  border-bottom: 1px solid #444;
  color: #ffffff;
}

.card-body {
  background-color: #2a2a3b;
  color: #ffffff;
}

.table {
  color: #ffffff;
  background-color: #2a2a3b;
}

.table-dark {
  background-color: #1e1e2f;
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: #3a3a4e;
}

.list-group-item {
  background-color: #2a2a3b;
  border: 1px solid #444;
  color: #ffffff;
}

.list-group-item:hover {
  background-color: #3a3a4e;
}

.alert {
  border-radius: 4px;
}

.alert-info {
  background-color: #0d6efd;
  border-color: #007bff;
  color: #ffffff;
}

.alert-success {
  background-color: #198754;
  border-color: #28a745;
  color: #ffffff;
}

.alert-warning {
  background-color: #ffc107;
  border-color: #ffb300;
  color: #000000;
}

.alert-danger {
  background-color: #dc3545;
  border-color: #c82333;
  color: #ffffff;
}

.text-muted {
  color: #b0b0c0 !important;
}

.spinner-border {
  border-color: #007bff;
}

@media (max-width: 600px) {
  .main-title {
    font-size: 2em;
    padding: 20px 10px;
  }
}
</style>