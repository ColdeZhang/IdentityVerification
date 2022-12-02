<template>

  <Steps />

  <n-divider />

  <main class="panel">
    <h2>注册本服务器外置登录账号</h2>
    <n-divider />
    <Registration />
    <n-divider />
    <n-button type="info" style="width: 100%;">我有正版账号</n-button>
  </main>
</template>


<script setup lang="ts">
import Registration from '../components/Registration.vue'
import Steps from '../components/Steps.vue'
import { NButton, NDivider, NGrid, NGridItem } from 'naive-ui'
</script>

<style>

.panel {
    margin: auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    text-align: left;
}

</style>