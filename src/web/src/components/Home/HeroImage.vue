<template>
  <div class="heroFrame">
    <div class="leftPart">
      <div class="title">Loading......</div>
      <div class="brief">Loading......</div>
      <v-btn
        x-large
        color="warning"
        elevation="18"
        width="190"
        min-height="60"
        outlined="true"
        >加入服务器</v-btn
      >
    </div>
    <div class="rightPart"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
</script>

<style>
@import "@/assets/css/Home/Hero.css";
</style>
