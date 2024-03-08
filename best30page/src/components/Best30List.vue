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

const getAssets = (a: string, surround: boolean = false) => {
  let call = 'http://localhost:61616/arcapi/v1/res/assets?path=' + a
  return surround ? `url('${call}')` : call
}

const b30: Ref<Array<Best30Details>> = ref(await useNativeAPI('b30'))


provide('diff_PAST', getAssets("img/multiplayer/ingame-diff-pst.png",true))
provide('diff_PRESENT', getAssets("img/multiplayer/ingame-diff-prs.png",true))
provide('diff_FUTURE', getAssets("img/multiplayer/ingame-diff-ftr.png",true))
provide('diff_BEYOND', getAssets("img/multiplayer/ingame-diff-byd.png",true))
provide('diff_ETERNAL', getAssets("img/multiplayer/ingame-diff-etr.png",true))
//
// case 0 -> "fail";
// case 1 -> "normal";
// case 2 -> "full";
// case 3 -> "pure";
// case 4 -> "easy";
// case 5 -> "hard";
provide('clear_0', getAssets("img/clear_type/fail.png", ))
provide('clear_1', getAssets("img/clear_type/normal.png", ))
provide('clear_2', getAssets("img/clear_type/full.png", ))
provide('clear_3', getAssets("img/clear_type/pure.png", ))
provide('clear_4', getAssets("img/clear_type/easy.png", ))
provide('clear_5', getAssets("img/clear_type/hard.png", ))

for (let a of ['explus', 'ex', 'aa', 'a', 'b', 'c', 'd']) {
  provide(`score_${a}`,getAssets(`img/grade/mini/${a}.png`))
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
