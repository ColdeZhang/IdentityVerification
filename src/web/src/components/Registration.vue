<template>
    <h2>注册外置登录账号</h2>
    <n-divider />
    <n-form>
        <n-grid :x-gap="12">
            <n-grid-item span="24">
                <n-form-item label="电子邮箱">
                    <n-input @input="checkEmail" placeholder="email@example.cn之类的东西" v-model:value="email" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item label="设置密码">
                    <n-input @input="ifEverythingPassed" type="password" show-password-on="click" placeholder="嘘..." v-model:value="password" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="创建一个游戏昵称">
                    <n-input @input="ifEverythingPassed" placeholder="就是游戏里的名字" v-model:value="profile_name" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button :disabled="profile_name_btn_disabled" @click="onProfileNameBtnClicked" style="width: 100%">{{ profile_name_text }}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="邀请码">
                    <n-input @input="ifEverythingPassed" placeholder="记得点一下验证" v-model:value="invite_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button :disabled="verify_invite_code_btn_disabled" @click="onInviteCodeBtnClicked" style="width: 100%">{{ verify_invite_code_text }}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="邮箱验证码">
                    <n-input @input="ifEverythingPassed" :disabled="!email_code_sended" placeholder="请先点击发送" v-model:value="email_code" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="12">
                <n-form-item label="&nbsp;">
                    <n-button :disabled="send_code_btn_disabled" @click="onSendEmail"
                        style="width: 100%">{{ send_code_text }}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item>
                    <n-button :disabled="submit_btn_disabled" type="primary" @click="onSubmitBtnClicked " style="width: 100%;">{{submit_btn_text}}</n-button>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24"><n-divider /></n-grid-item>
            

            <n-grid-item span="12">
                 <n-button @click="$emit('geniue_btn_pressed')"  type="info" style="width: 100%;">我有正版账号</n-button>
            </n-grid-item>

            <n-grid-item span="12">
                <n-button @click="$emit('ingeniue_btn_pressed')" type="primary" style="width: 100%;">我已经有账号了</n-button>
            </n-grid-item>

            
        </n-grid>
    </n-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { NButton, NForm, NFormItem, NInput, NGrid, NGridItem, useDialog, useNotification, NDivider } from 'naive-ui'
import type { NotificationType } from 'naive-ui'
import axios from "axios";

const email = ref('')
const profile_name = ref('')
const email_code = ref('')
const password = ref('')
const invite_code = ref('')

const send_code_text = ref('发送验证码')
const send_code_btn_disabled = ref(false)

const verify_invite_code_text = ref('验证邀请码')
const verify_invite_code_btn_disabled = ref(false)


const profile_name_text = ref('验证可用性')
const profile_name_btn_disabled = ref(false)

const submit_btn_text = ref('注册外置登录')
const submit_btn_disabled = ref(true)

const profile_name_passed = ref(false)
const invite_code_passed = ref(false)
const email_code_sended = ref(false)

const dialog = useDialog()  // 弹窗
const notification = useNotification()  // 通知

const emit = defineEmits(['ingeniue_btn_pressed', 'geniue_btn_pressed'])

// 检查是否可以启动提交
function ifEverythingPassed() {
    if (profile_name_passed.value && invite_code_passed.value && email_code_sended.value && email.value != '' && password.value != '' && email_code.value != '') {
        submit_btn_disabled.value = false
    }
}

// 发送邮件验证码
function onSendEmail() {
    if (email.value == '') {
        dialog.error({
            title: '邮箱不正确',
            content: '请输入你的邮箱地址！',
            positiveText: '知道了',
        })
        return
    }
    if (checkEmail(email.value) == false) {
        dialog.error({
            title: '邮箱不正确',
            content: '邮箱的格式有误，请重新输入！',
            positiveText: '知道了',
        })
        return
    }
    send_code_text.value = '发送中...'
    send_code_btn_disabled.value = true
    axios.post('/api/getEmailCode', {
        email: email.value
    }).then(function (response: any) {
        if (response.data.code != 200) {
            dialog.error({
                title: '发送失败',
                content: response.data.msg,
                positiveText: '知道了',
            })
            return
        }
        notify('success', '成功', response.data.msg)
        email_code_sended.value = true
        sendMailCountDown();
        ifEverythingPassed()
    });
}

// 验证昵称
function onProfileNameBtnClicked() {
    if (profile_name.value == '') {
        dialog.error({
            title: '昵称错误',
            content: '请输入你的昵称！',
            positiveText: '知道了',
        })
        return
    }
    if (checkProfileName(profile_name.value) == false) {
        dialog.error({
            title: '昵称错误',
            content: '昵称的格式有误,只允许英文数字下划线,请重新输入！',
            positiveText: '知道了',
        })
        return
    }
    profile_name_btn_disabled.value = true
    axios.post('/api/verifyName', {
        profile_name: profile_name.value
    }).then(function (response: any) {
        if (response.data.code != 200) {
            dialog.warning({
                title: '这个似乎行不通...',
                content: response.data.msg,
                positiveText: '知道了',
            })
            profileNameCountDown();
            return
        }
        notify('success', '成功', response.data.msg)
        profile_name_text.value = '通过'
        profile_name_passed.value = true
        profile_name_btn_disabled.value = false
        ifEverythingPassed()
    });
}

// 验证邀请码按钮
function onInviteCodeBtnClicked() {
    verify_invite_code_btn_disabled.value = true
    axios.get('/api/verifyCode', {
        params: {
            code: invite_code.value
        }
    }).then(function (response: any) {
        if (response.data.code != 200) {
            dialog.error({
                title: '你是谁？',
                content: response.data.msg,
                positiveText: '知道了',
            })
            inviteCodeCountDown();
            return
        }
        notify('success', '成功', response.data.msg)
        invite_code_passed.value = true
        verify_invite_code_text.value = '由 ' + response.data.data.inviter_name + ' 邀请'
        ifEverythingPassed()
    });
}

// 等待三十秒才能再次验证邀请码
function inviteCodeCountDown() {
    let count = 30
    let timer = setInterval(() => {
        count--
        verify_invite_code_text.value = '等待 ' + count + ' 秒'
        if (count == 0) {
            clearInterval(timer)
            verify_invite_code_text.value = '验证邀请码'
            verify_invite_code_btn_disabled.value = false
        }
    }, 1000)
}

// 60秒后才能再次发送验证码
function sendMailCountDown() {
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

// 30秒后才能再次校验用户名
function profileNameCountDown() {
    let time = 30
    let timer = setInterval(() => {
        time--
        profile_name_text.value = time + '秒后重试'
        if (time == 0) {
            clearInterval(timer)
            profile_name_text.value = '再次尝试'
            profile_name_btn_disabled.value = false
        }
    }, 1000)
}

// 正则表达式检查邮箱格式
function checkEmail(email: string) {
    ifEverythingPassed()
    let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    if (!reg.test(email)) {
        send_code_btn_disabled.value = true
        return false;
    }
    send_code_btn_disabled.value = false
    return true;
}

// 正则表达式检查昵称 只能包含英文数字下划线
function checkProfileName(profile_name: string) {
    let reg = /^[a-zA-Z0-9_]+$/;
    if (!reg.test(profile_name)) {
        return false;
    }
    return true;
}

function notify(type: NotificationType, content: string, meta: string) {
    notification[type]({
        content: content,
        meta: meta,
        duration: 5000,
        keepAliveOnHover: true
    })
}

// 提交按钮
function onSubmitBtnClicked(this: any) {
    submit_btn_disabled.value = true
    axios.post('/api/registration', {
        email: email.value,
        password: password.value,
        email_code: email_code.value,
        profile_name: profile_name.value,
        invite_code: invite_code.value,
    }).then(function (response: any) {
        if (response.data.code != 200) {
            dialog.error({
                title: '遇到问题了！',
                content: response.data.msg,
                positiveText: '知道了',
            })
            submitBtnCountDown();
            return
        }
        notify('success', '成功', response.data.msg)
        emit('ingeniue_btn_pressed')
    });
}

// 30秒后才能再提交
function submitBtnCountDown() {
    let time = 30
    let timer = setInterval(() => {
        time--
        submit_btn_text.value = time + '秒后重试'
        if (time == 0) {
            clearInterval(timer)
            submit_btn_text.value = '再试一次'
            submit_btn_disabled.value = false
        }
    }, 1000)
}


</script>
