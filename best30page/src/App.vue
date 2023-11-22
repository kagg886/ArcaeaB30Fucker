<script setup lang="ts">

import {sendMessage} from "./hook/nativeAPI.ts";

const getB30 = () => {
  sendMessage('test', {
    a: 1,
    b: 2,
  }).then(rtn => {
    console.log(rtn)
  })
}
</script>

<template>
  <button @click="getB30">sss</button>
</template>

<style scoped>

</style>
