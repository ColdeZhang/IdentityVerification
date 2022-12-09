<template>

    <h2>实名认证白名单</h2>
    <n-divider />
    <n-form>
        <n-grid :x-gap="12">

            <n-grid-item span="24">
                <n-form-item label="账户类型">
                    <n-radio-group v-model:value="(user_type as number)" name="radiobuttongroup1" style="width: 100%;">
                        <n-radio-button style="width: 50%; text-align: center" v-for="type in user_types" :key="type.value" :value="type.value" :label="type.label"/>
                    </n-radio-group>
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item label="游戏昵称">
                    <n-input @input="ifSubmitSatisifed" v-model:value="profile_name" placeholder="请输入游戏ID" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item label="身份证号码">
                    <n-input @input="ifSubmitSatisifed" v-model:value="id" placeholder="请输入18位身份证号码 本服务不会保存个人信息" />
                </n-form-item>
            </n-grid-item>

            <n-grid-item span="24">
                <n-form-item label="真实姓名">
                    <n-input @input="ifSubmitSatisifed" v-model:value="name" placeholder="此信息仅用于实名制认证" />
                </n-form-item>
            </n-grid-item>

            

            <n-grid-item span="24">
                <n-divider />
                <n-button :disabled="submit_btn_disabled" strong secondary @click="submit" type="warning" style="width: 100%;">{{submit_btn_text}}</n-button>
            </n-grid-item>

        </n-grid>
    </n-form>

</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { CSSProperties } from 'vue'
import type { NotificationType } from 'naive-ui';
import { NButton, NDivider, NForm, NFormItem, NGrid, NGridItem, NInput, NRadioGroup, NRadioButton, useDialog, useNotification } from 'naive-ui';
import axios from 'axios';

const props = defineProps(['user_type']);

const id = ref('');
const name = ref('');
const profile_name = ref('');

const submit_btn_text = ref('提交认证');
const submit_btn_disabled = ref(true);

const user_types: { value: number, label: string }[] = [
    { value: 0, label: '外置登录' },
    { value: 1, label: '正版账号' },
]

const dialog = useDialog()  // 弹窗
const notification = useNotification()  // 通知

const emit = defineEmits(['to-finish', 'to-previews']);

// 60秒后才能再次提交
function submitBtnCountDown() {
    let time = 60
    let timer = setInterval(() => {
        time--
        submit_btn_text.value = time + '秒后重试'
        if (time == 0) {
            clearInterval(timer)
            submit_btn_text.value = '发送验证码'
            submit_btn_disabled.value = false
        }
    }, 1000)
}

// 提交认证
function submit(this: any) {
    submit_btn_disabled.value = true
    submit_btn_text.value = '提交中...'
    axios.post('/api/signWhiteList', {
        id: id.value,
        name: name.value,
        username: profile_name.value,
        is_genuine: (props.user_type as boolean),
    }).then(res => {
        if (res.data.code != 200) {
            dialog.error({
                title: '好像出了点问题...',
                content: res.data.msg,
                positiveText: '创建一个外置登录账户',
                negativeText: '我再试试',
                onPositiveClick: () => {
                    // window.location.href = '/newjoin'
                    emit('to-previews')
                },
            })
            submit_btn_disabled.value = true
            submitBtnCountDown()
            return
        }
        notify('success', '验证通过', '一切看起来非常完美！')
        emit('to-finish')
    })
}

// 正则表达式判断身份证
function verifyId(){
    if (id.value.length != 18) {
        return false
    }
    const reg = /^[0-9x]+$/;
    return reg.test(id.value);
}

function ifSubmitSatisifed() {
    if (verifyId() && name.value.length > 0 && profile_name.value.length > 0) {
        submit_btn_disabled.value = false
    } else {
        submit_btn_disabled.value = true
    }
}

function notify(type: NotificationType, content: string, meta: string) {
    notification[type]({
        content: content,
        meta: meta,
        duration: 5000,
        keepAliveOnHover: true
    })
}
      

</script>