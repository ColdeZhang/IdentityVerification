<template>
  <header>
    <RouterLink to="/">主页</RouterLink> |
    <RouterLink to="/reg">注册</RouterLink>
  </header>
  <main>
    <RouterView />
  </main>
</template>


<script setup lang="ts">
import { h } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { NMenu, NGrid, NGridItem, NLayout, NLayoutHeader, NLayoutContent, NLayoutFooter } from 'naive-ui';
import type { MenuOption } from 'naive-ui'

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
