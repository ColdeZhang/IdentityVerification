<template>
  <v-toolbar-title class="titleText">Loading...</v-toolbar-title>

  <v-spacer></v-spacer>

  <v-btn class="mainButton">相册</v-btn>
  <v-btn class="mainButton">卫星地图</v-btn>
  <v-btn class="mainButton">公约</v-btn>
  <v-btn class="mainButton">统计榜单</v-btn>

  <!-- <v-btn color="warning" class="mainButton">白名单认证</v-btn> -->
  <v-btn color="warning" class="mainButton">注册</v-btn>
  <v-btn color="blue" class="mainButton">登录</v-btn>
</template>

<script setup lang="ts"></script>

<style>
@import "../assets/css/HeaderBar.css";
</style>
