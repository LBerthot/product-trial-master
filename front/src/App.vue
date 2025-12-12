<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <header class="header">
    <router-link to="/">Home</router-link>
    <router-link to="/login">Login</router-link>
    <router-link to="/products">Products</router-link>
  </header>
  <router-view />
</template>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
