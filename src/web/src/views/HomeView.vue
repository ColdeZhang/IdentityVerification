<template>

    <n-h1>{{server_name}}</n-h1>
    <n-h3>{{server_version}}</n-h3>
    <n-h3>{{server_url}}</n-h3>
    <n-h2>{{server_description}}</n-h2>

    <n-button @click="join" strong secondary type="primary" size="large" >加入游戏</n-button>

</template>

<script setup lang="ts">
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { NH1, NH2, NH3, NButton } from 'naive-ui';

const server_name = ref('Loading...')
const server_version = ref('Loading...')
const server_description = ref('Loading...')
const server_url = ref('Loading...')

// 加载页面时
onMounted(() => {
    axios.get('/api/webmeta').then((response) => {
        console.log(response.data)
        server_name.value = "欢迎来到 " + response.data.data.title
        server_version.value = "当前服务器版本：" + response.data.data.version
        server_url.value = "服务器地址：" + response.data.data.serverUrl
        server_description.value = response.data.data.description
    });
});

function join(){
    window.location.href = '/newjoin'
}

</script>