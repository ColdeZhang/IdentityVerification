<template>

<n-grid :x-gap="12" :y-gap="12">

<n-grid-item span="24">
    <n-h2>#1 下载一些东西</n-h2>
</n-grid-item>

<n-grid-item span="24">
    <n-h5>下面的按钮从左向右依次是我们推荐的启动器、java运行环境、微软官方启动器。</n-h5>
    <n-h5>如果你是正版账号用户，你最好选择正版启动器。因为正版启动器的使用十分简易，只是安装速度拉胯。（但是不需要自己安装Java）</n-h5>
    <n-h5>如果你是外置登录用户，请下载我们推荐的启动器，同时需要下载Java并安装。假如你有更喜欢的启动器当然也可以使用自己最习惯的，但是请记住我们没有对其进行过测试，如果存在使用问题只能自行处理。</n-h5>
</n-grid-item>

<n-grid-item span="8">
    <n-button type="primary" @click="recommend" style="width: 100%;">推荐的启动器</n-button>
</n-grid-item>

<n-grid-item span="8">
    <n-button type="info" @click="download_java" style="width: 100%;">Java</n-button>
</n-grid-item>

<n-grid-item span="8">
    <n-button type="warning" @click="click_btn_to_microsoft" style="width: 100%;">正版启动器</n-button>
</n-grid-item>

<n-grid-item span="24"><n-divider /></n-grid-item>


<n-grid-item span="24">
    <n-h2>#2 登录账户并安装游戏</n-h2>
</n-grid-item>

<n-grid-item span="20" offset="2">
    <n-timeline>
        <n-timeline-item type="success" title="打开启动器" content="就是双击启动，记得给启动器新建一个文件夹单独存放" />
        <n-timeline-item type="success" title="添加账户" content="在左上角，点那个史蒂夫头像" />
        <n-timeline-item type="success" title="添加认证服务器" content="左下角的加号 下面有行小字（外置登录）" />
        <n-timeline-item type="success" title="输入这个地址" :content="yag_server" />
        <n-timeline-item type="success" title="点击下一步、完成" content="此时应该已经可以看到我们服务器的信息了" />
        <n-timeline-item type="success" title="点击刚刚添加的认证" content="应该就在“微软账户”下面" />
        <n-timeline-item type="success" title="登录外置登录账号" content="刚刚第一步注册账号的邮箱密码" />
        <n-timeline-item type="info" title="点左上角的返回" content="回到启动器刚打开的界面" />
        <n-timeline-item type="success" title="添加游戏版本" content="点“没有游戏版本”，下面有小字（进入下载页安装游戏）" />
        <n-timeline-item type="success" title="点击安装新版本" :content="current_version" />
        <n-timeline-item type="success" title="安装游戏" content="点击右下角的安装等待完成后回到主页面" />
        <n-timeline-item type="success" title="大功告成！" :content="server_url" />
    </n-timeline>
</n-grid-item>

<n-grid-item span="24"><n-divider /></n-grid-item>

<n-grid-item span="24">
    <n-button type="primary" @click="copy_to_clipboard" style="width: 100%;">点击复制</n-button>
</n-grid-item>

<n-grid-item span="24"><n-divider /></n-grid-item>

<n-grid-item span="24">
    <n-button type="primary" @click="back_to_home" style="width: 100%;">回到主页</n-button>
</n-grid-item>


</n-grid>



</template>

<script setup lang="ts">
import { NH2, NP, NButton, NGridItem, NGrid, NH5, NDivider, NTimeline, NTimelineItem, useDialog, useNotification } from 'naive-ui';
import { ref, onMounted } from 'vue';
import axios from 'axios';

const yag_server = ref('Loading...')
const current_version = ref('本服务目前的版本是: Loading...') // TODO: 从服务器获取
const server_url = ref("启动游戏选择多人，输入本服务器地址: Loading...") // TODO: 从服务器获取

const dialog = useDialog()
const notification = useNotification()  // 通知

function click_btn_to_microsoft(){
    dialog.info({
        title: '你居然要下载这个',
        content: '说实话我觉得这个选项挺废的...因为从逻辑上来说你如果有正版账号你应该不需要这个按钮，那我只能当你觉得微软官网找起来比较麻烦了。',
        positiveText: '是的太麻烦了，带我去',
        negativeText: '不了，我就开个玩笑',
        onPositiveClick: () => {
            window.open('https://www.minecraft.net/zh-hans/get-minecraft')
        }
    })
}

function download_java(){
    dialog.info({
        title: '关于Java',
        content: '因为不同系统需要不同的Java，比较杂乱。所以这里我们会带你前往Java官网下载。',
        positiveText: '前往下载',
        negativeText: '取消',
        onPositiveClick: () => {
            window.open('https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html')
        }
    })
}

function recommend(){
    window.open('https://hmcl.huangyuhui.net')
}

function back_to_home() {
    window.location.href = '/'
}

// 点击后复制内容到剪贴板
function copy_to_clipboard() {
    const input = document.createElement('input');
    input.setAttribute('readonly', 'readonly');
    input.setAttribute('value', yag_server.value);
    document.body.appendChild(input);
    input.select();
    if (document.execCommand('copy')) {
        document.execCommand('copy');
        notification.info({
            title: '复制成功',
            content: '赶快前往启动器粘贴吧！',
            duration: 5000
        })
    }
    document.body.removeChild(input);
}

// 加载页面时
onMounted(() => {
    axios.get('/api/webmeta').then((response) => {
        console.log(response.data)
        current_version.value = "本服务目前的版本是: " + response.data.data.version
        server_url.value = "启动游戏选择多人，输入本服务器地址: " + response.data.data.serverUrl
        yag_server.value = response.data.data.yagUrl
    });
});


</script>