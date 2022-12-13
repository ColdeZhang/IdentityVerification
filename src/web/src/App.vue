<template>
  <v-app>
    <v-app-bar app>
      <HeaderBar />
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>
  </v-app>

  <v-footer app></v-footer>
</template>

<script setup lang="ts">
import HeaderBar from "./layouts/HeaderBar.vue";
</script>
