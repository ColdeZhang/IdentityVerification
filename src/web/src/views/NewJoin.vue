<template>

  <Steps :current=current_step />

  <n-divider />

  <main v-if="(current_step === 1)" class="panel">
    <Registration @geniue_btn_pressed="geniue_user" @ingeniue_btn_pressed="ingeniue_user" />
  </main>
  
  <main v-if="(current_step === 2)" class="panel">
    <WhiteList @to-finish="whitelist_to_finish" @to-previews="whitelist_to_register" :user_type="is_geniue" />
  </main>

  <main v-if="(current_step === 3)" class="panel">
    <Finish />
  </main>

</template>


<script setup lang="ts">
import Registration from '../components/Registration.vue'
import WhiteList from '../components/WhiteList.vue'
import Finish from '../components/Finish.vue'
import Steps from '../components/Steps.vue'
import { ref } from 'vue'
import { NButton, NDivider, NGrid, NGridItem } from 'naive-ui'

const is_geniue = ref(1)

const current_step = ref(3)

function geniue_user() {
  is_geniue.value = 1
  current_step.value = 2
}

function ingeniue_user() {
  is_geniue.value = 0
  current_step.value = 2
}

function whitelist_to_register() {
  current_step.value = 1
}

function whitelist_to_finish() {
  current_step.value = 3
}

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