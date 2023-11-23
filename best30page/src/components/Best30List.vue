<template>
  <div class="container">
    <Suspense v-for="a in b30">
      <Card :b30="a"/>
    </Suspense>

    <div class="tips">
      <div>Powered by kagg886</div>
      <div>Github: https://github.com/kagg886/ArcaeaB30Fucker</div>
    </div>
  </div>
</template>

<script async setup lang="ts">
import {useNativeAPI} from "../hook/nativeAPI.ts";
import {Ref, ref} from "vue";
import {Best30Details} from "../hook/type.ts";
import Card from "./Card.vue";

const b30: Ref<Array<Best30Details>> = ref(await useNativeAPI('b30'))
// {
//   "data": {
//   "clearStatus": 1,
//       "difficulty": "BEYOND",
//       "farCount": 18,
//       "health": 100,
//       "id": "tempestissimo",
//       "lostCount": 10,
//       "perfectCount": 1512,
//       "score": 9877987,
//       "shinyPerfectCount": 1364
// },
//   "ex_diff": 11.5,
//     "name": "Tempestissimo",
//     "ptt": 12.89
// }
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.container:last-child {
  margin-bottom: 100px;
}

.tips {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: end;
  font-size: 10px;
  font-weight: 800;
  font-family: 'Andrea', serif;
}
</style>
