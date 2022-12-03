<template>
    <n-steps :current="(current as number)">
      <n-step
        title="注册账号"
        description="如果你有正版账号可以跳过这一步"
      />
      <n-step
        title="实名认证"
        description="实名认证以登记服务器白名单"
      />
      <n-step
        title="完成！"
        description="准备就绪，欢迎加入我们～"
      />
    </n-steps>
</template>

<script setup lang="ts">

import { NStep, NSteps } from 'naive-ui';
import { ref } from 'vue';

const current = ref(1)
const currentStatus = ref('process')

</script>