<script async setup lang="ts">
import {Best30Details} from "../hook/type.ts";
import {useNativeAPI} from "../hook/nativeAPI.ts";
import {ref} from "vue";

const props = defineProps<{
  b30: Best30Details
}>()
const bg = ref()
const diff = ref()

useNativeAPI('loadArcaeaSong', props.b30.data).then((res) => {
  bg.value = "url('data:image/jpeg;base64," + res + "')"
})

let d;
switch (props.b30.data.difficulty) {
  case 'PAST':
    d = 'pst'
    break
  case 'PRESENT':
    d = 'prs'
    break
  case 'FUTURE':
    d = 'ftr'
    break
  case 'BEYOND':
    d = 'byd'
    break
}
useNativeAPI('assets', "img/multiplayer/ingame-diff-" + d + ".png").then((call) => {
  diff.value = "url('data:image/jpeg;base64," + call + "')"
})

const p_bg = ref()
const f_bg = ref()
const l_bg = ref()

//TODO need fix
useNativeAPI('assets', "layouts/1080/results/pure-count.png").then((call) => {
  p_bg.value = "url('data:image/jpeg;base64," + call + "')"
})
useNativeAPI('assets', "layouts/1080/results/far-count.png").then((call) => {
  f_bg.value = "url('data:image/jpeg;base64," + call + "')"
})
useNativeAPI('assets', "layouts/1080/results/lost-count.png").then((call) => {
  l_bg.value = "url('data:image/jpeg;base64," + call + "')"
})

</script>

<template>
  <canvas ref="canvas" style="display: none"></canvas>
  <div class="card">
    <div class="name">
      {{ b30.name.substring(0, Math.min(15, b30.name.length)) }}{{ b30.name.length > 15 ? '...' : '' }}
    </div>
    <ul>
      <li style="background-image: v-bind(p_bg)">
        pure: {{ b30.data.perfectCount }}({{ b30.data.shinyPerfectCount }})
      </li>
      <li style="background-image: v-bind(f_bg)">
        far: {{ b30.data.farCount }}
      </li>
      <li style="background-image: v-bind(l_bg)">
        lost: {{b30.data.lostCount}}
      </li>
    </ul>
  </div>
</template>

<style scoped>

li {
  font-size: 10px;
}

.card {
  width: 45%;
  height: 15%;
  border: 1px solid black;
  border-radius: 10px;
  margin-bottom: 5%;
  overflow: hidden;

  animation: an 1s;

  background-image: v-bind(bg);
  background-position: center, center;
  background-repeat: no-repeat;
  background-attachment: scroll;
  background-size: cover;
}

@keyframes an {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.name {
  font-family: 'ExoMedium', serif;
  font-size: 10px;
  padding-left: 3px;
  padding-top: 3px;
  background-image: v-bind(diff), linear-gradient(to right, rgba(255, 255, 255), rgba(0, 0, 0, 0));
  background-position: top right;
  background-repeat: no-repeat;
}

.name::before {
  content: "";
  border-left: 3px solid #AA00AB;
  margin-right: 3px;
}
</style>