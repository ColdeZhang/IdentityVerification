<template>
    <n-form>
        <n-grid :x-gap="12">
            <n-grid-item span="24">
                <n-form-item label="电子邮箱">
                    <n-input placeholder="" v-model:value="email" />
                </n-form-item>
            </n-grid-item>
            
            <n-grid-item span="12">
                <n-form-item label="验证码">
                    <n-input placeholder="" v-model:value="email_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button @click="send_email_code" style="width: 100%">{{send_code_text}}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item label="密码">
                    <n-input type="password" show-password-on="click" placeholder="" v-model:value="password" />
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

const email = ref('')
const email_code = ref('')
const password = ref('')
const invite_code = ref('')

const send_code_text = ref('发送验证码')
const verify_invite_code_text = ref('验证邀请码')


const onSubmit = () => {
    console.log(email.value, email_code.value, password.value)
}

const send_email_code = () => {
    console.log('send email code')
}


</script>
