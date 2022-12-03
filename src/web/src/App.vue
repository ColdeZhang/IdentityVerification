<template>
  <header>
    <RouterLink to="/">主页</RouterLink> |
    <RouterLink to="/reg">注册</RouterLink>
  </header>
  <main>

    <n-dialog-provider>
      <n-notification-provider>
        <RouterView />
      </n-notification-provider>
    </n-dialog-provider>

  </main>
</template>


<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { NDialogProvider, NNotificationProvider } from 'naive-ui';
import axios from "axios";

axios.defaults.baseURL = 'http://127.0.0.1:11520/';

</script>


<style scoped>
header {
  position: fixed;
  width: 100%;
  top: 0;
  left: 0%;
  color: rgb(105, 105, 105);
  padding: 1em;
  padding-left: 5rem;
  background-color: rgb(255, 255, 255);
}

main {
  margin: auto;
  padding: 1em;
  position: flex;
}
</style>
