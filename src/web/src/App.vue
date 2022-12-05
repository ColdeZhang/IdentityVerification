<template>

  <n-config-provider :theme="theme">
    <header>
    </header>
    <headerText>
      |<RouterLink to="/"> <menuBtn>主页</menuBtn></RouterLink>|
      <RouterLink to="/newjoin"><menuBtn>注册外置登录</menuBtn></RouterLink>|
      <RouterLink to="/newjoin"><menuBtn>实名认证白名单</menuBtn></RouterLink>|
    </headerText>
    <main>

      <n-dialog-provider>
        <n-notification-provider>
          <RouterView />
        </n-notification-provider>
      </n-dialog-provider>

    </main>
  </n-config-provider>
</template>


<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { NDialogProvider, NNotificationProvider, NConfigProvider, useOsTheme, darkTheme, NH2 } from 'naive-ui';
import axios from "axios";
import { computed } from 'vue';

axios.defaults.baseURL = 'http://127.0.0.1:11520/';

const osThemeRef = useOsTheme()
const theme = computed(() => (osThemeRef.value === 'dark' ? darkTheme : null))

</script>


<style scoped>
header {
  position: fixed;
  width: 100%;
  top: -10px;
  left: 0%;
  height: 65px;
  color: rgb(19, 10, 10);
  background-color: rgba(255, 255, 255, 0.9);
  padding: 1em;
  z-index: 100;
  filter: blur(5px);
}

headerText {
  position: fixed;
  width: 100%;
  top: 0;
  left: 0%;
  color: rgb(19, 10, 10);
  padding: 1em;
  padding-left: 5rem;
  z-index: 101;
}

menuBtn{
  top: 0;
  left: 0;
  padding: 2em;
  font-size: larger;
  font-weight: bold;
}


main {
  margin: auto;
  padding: 1em;
  position: flex;
  margin-top: 5rem;
}
</style>
