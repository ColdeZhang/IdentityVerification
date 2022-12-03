<template>
    <n-form>
        <n-grid :x-gap="12">
            <n-grid-item span="24">
                <n-form-item label="电子邮箱">
                    <n-input @input="checkEmail" placeholder="" v-model:value="email" />
                </n-form-item>
            </n-grid-item>
            
            <n-grid-item span="24">
                <n-form-item label="设置密码">
                    <n-input type="password" show-password-on="click" placeholder="" v-model:value="password" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="创建一个游戏昵称">
                    <n-input placeholder="" v-model:value="invite_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button @click="send_email_code" style="width: 100%">{{profile_name_text}}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="邀请码">
                    <n-input placeholder="" v-model:value="invite_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button @click="send_email_code" style="width: 100%">{{verify_invite_code_text}}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="邮箱验证码">
                    <n-input placeholder="" v-model:value="email_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button :disabled="send_code_btn_disabled" @click="getEmailCode" style="width: 100%">{{send_code_text}}</n-button>
                </n-form-item>
            </n-grid-item>
        
            <n-grid-item span="24">
                <n-form-item>
                    <n-button type="primary" @click="onSubmit" style="width: 100%;">注册外置登录</n-button>
                </n-form-item>
            </n-grid-item>
        </n-grid>
    </n-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { NButton, NForm, NFormItem, NInput, NGrid, NGridItem } from 'naive-ui'
import axios from "axios";

const email = ref('')
const email_code = ref('')
const password = ref('')
const invite_code = ref('')

const send_code_text = ref('发送验证码')
const send_code_btn_disabled = ref(false)

const verify_invite_code_text = ref('验证邀请码')
const profile_name_text = ref('验证可用性')


const onSubmit = () => {
    console.log(email.value, email_code.value, password.value)
}

const send_email_code = () => {
    console.log('send email code')
}

function getEmailCode() {
    if (email.value == '') {
        alert('请输入邮箱')
        return
    }
    if (checkEmail(email.value) == false) {
        alert('邮箱格式不正确')
        return
    }
    send_code_text.value = '发送中...'
    send_code_btn_disabled.value = true
    axios.post('/api/getEmailCode', {
        email: email.value
    }).then(function (response) {
        send_code_text.value = '发送成功'
        console.log(response);
    }).catch(function (error) {
        console.log(error);
    });
    countDown();
}

// 60秒后才能再次发送验证码
function countDown() {
    let time = 60
    let timer = setInterval(() => {
        time--
        send_code_text.value = time + '秒后重试'
        if (time == 0) {
            clearInterval(timer)
            send_code_text.value = '发送验证码'
            send_code_btn_disabled.value = false
        }
    }, 1000)
}

// 正则表达式检查邮箱格式
function checkEmail(email) {
    let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    if (!reg.test(email)) {
        return false;
    }
    return true;
}


</script>
