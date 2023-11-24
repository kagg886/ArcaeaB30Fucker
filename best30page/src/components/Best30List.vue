<template>
  <div class="container">

    <Card v-for="(a,index) in b30" :data="a" :index="index"/>

    <div class="tips">
      <div>Powered by kagg886</div>
      <div>Github: https://github.com/kagg886/ArcaeaB30Fucker</div>
    </div>
  </div>
</template>

<script async setup lang="ts">
import {useNativeAPI} from "../hook/nativeAPI.ts";
import {provide, Ref, ref} from "vue";
import {Best30Details} from "../hook/type.ts";
import Card from "./Card.vue";
const getAssets = async (a: string, surround: boolean = true) => {
  return useNativeAPI('assets', a).then((call) => {
    return surround ? "url('data:image/png;base64," + call + "')" : "data:image/png;base64," + call;
  })
}

const b30: Ref<Array<Best30Details>> = ref(await useNativeAPI('b30'))


provide('diff_PAST', await getAssets("img/multiplayer/ingame-diff-pst.png"))
provide('diff_PRESENT', await getAssets("img/multiplayer/ingame-diff-prs.png"))
provide('diff_FUTURE', await getAssets("img/multiplayer/ingame-diff-ftr.png"))
provide('diff_BEYOND', await getAssets("img/multiplayer/ingame-diff-byd.png"))
//
// case 0 -> "fail";
// case 1 -> "normal";
// case 2 -> "full";
// case 3 -> "pure";
// case 4 -> "easy";
// case 5 -> "hard";
provide('clear_0', await getAssets("img/clear_type/fail.png", false))
provide('clear_1', await getAssets("img/clear_type/normal.png", false))
provide('clear_2', await getAssets("img/clear_type/full.png", false))
provide('clear_3', await getAssets("img/clear_type/pure.png", false))
provide('clear_4', await getAssets("img/clear_type/easy.png", false))
provide('clear_5', await getAssets("img/clear_type/hard.png", false))

for (let a of ['explus','ex','aa','a','b','c','d']) {
  provide(`score_${a}`, await getAssets(`img/grade/mini/${a}.png`, false))
}
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.tips {
  color: white;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: end;
  font-size: 10px;
  font-weight: 800;
  font-family: 'Andrea', serif;
}
</style>
