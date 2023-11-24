<script async setup lang="ts">
import {Best30Details} from "../hook/type.ts";
import {useNativeAPI} from "../hook/nativeAPI.ts";
import {computed, inject, ref} from "vue";

const props = defineProps<{
  data: Best30Details,
  index: number
}>()
const bg = ref()
const diff = ref(inject('diff_' + props.data.data.difficulty))

useNativeAPI('loadArcaeaSong', props.data.data).then((res) => {
  bg.value = "url('data:image/jpeg;base64," + res + "')"
})
const random = ref((Math.random() + 0.5) + 's')

const getScoreType = computed(() => {
  let scoreType;
  let data = props.data.data.score
  if (data > 9900000) {
    scoreType = "explus";
  } else if (data > 9800000) {
    scoreType = "ex";
  } else if (data > 9500000) {
    scoreType = "aa";
  } else if (data > 9200000) {
    scoreType = "a";
  } else if (data > 8900000) {
    scoreType = "b";
  } else if (data > 8600000) {
    scoreType = "c";
  } else {
    scoreType = "d";
  }
  return 'score_' + scoreType
})
</script>

<template>
  <div class="card">
    <div class="name">
      #{{ index + 1 }} {{ data.name.substring(0, Math.min(15, data.name.length)) }}{{
        data.name.length > 15 ? '...' : ''
      }}
    </div>
    <ul>
      <li>
        <span :style="{color: '#09A1DD'}">P</span>
        <span>{{ data.data.perfectCount }}({{ data.data.shinyPerfectCount }})</span>
      </li>
      <li>
        <span :style="{color: '#D68F38'}">F</span>
        <span>{{ data.data.farCount }}</span>
      </li>
      <li>
        <span :style="{color: '#A77ECD'}">L</span>
        <span>{{ data.data.lostCount }}</span>
      </li>
      <li class="score">
        <ul>
          <li>
            {{ data.data.score }}
          </li>
          <li>
            <img :src="inject(getScoreType)" alt="">
            <img :src="inject('clear_' + data.data.clearStatus)" alt="">
          </li>
        </ul>
      </li>
    </ul>

    <ol class="center">
      <li>
        {{ data.ex_diff }}
      </li>
      <li style="transition: all 0s; transform: rotateZ(90deg) translateX(7px)">
        →
      </li>
      <li>
        {{ data.ptt }}
      </li>
    </ol>
  </div>
</template>

<style scoped>

.center {
  position: absolute;
  left: 30%;
  top: 50%;
}

.center > li {
  font-weight: 800;
  font-family: 'Ramaraja', serif;
  font-size: 10px;
}

img {
  width: 35px;
  height: 35px;
}

.score {
  right: 0;
  top: 0;
  position: absolute;
}

.score > ul {
  text-align: right;
}

.score > ul:first-child {
  padding-right: 3px;
  font-size: 18px;
  -webkit-text-stroke: 1px white;
}

span {
  font-size: 10px;
  margin-left: 5px;
  margin-right: 5px;
  font-family: 'ExoSemiBold', serif;
}

.card > ul {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-items: center;
  background-image: linear-gradient(to right, rgba(255, 255, 255, 70%), rgba(0, 0, 0, 0));

  position: relative;
}

li {
  margin-bottom: -2px;
}

.card {
  width: 45%;
  height: 15%;
  border: 1px solid black;
  border-radius: 10px;
  margin-bottom: 5%;
  overflow: hidden;
  position: relative;

  animation: an v-bind(random);

  background-image: v-bind(bg);
  background-position: center, center;
  background-repeat: no-repeat;
  background-attachment: scroll;
  background-size: cover;
}

@keyframes an {
  from {
    opacity: 0;
    transform: translateY(50px);
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