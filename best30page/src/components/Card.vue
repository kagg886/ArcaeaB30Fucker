<script async setup lang="ts">
import {Best30Details, SongDetails} from "../hook/type.ts";
import {computed, inject, ref} from "vue";
import CustomDialog from "./CustomDialog.vue";
import {useNativeAPI} from "../hook/nativeAPI.ts";

const props = defineProps<{
  data: Best30Details,
  index: number
}>()
const bg = ref()
const diff = ref(inject('diff_' + props.data.data.difficulty))

bg.value = `url('http://localhost:61616/arcapi/v1/res/image?id=${props.data.data.id}&difficulty=${props.data.data.difficulty}')`
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


const dialog = ref(false)
const openDialog = () => {
  if (songDetails.value.bpm == undefined) {
    useNativeAPI('songInfo', {
      id: props.data.data.id
    }).then((v: SongDetails) => {
      songDetails.value = v

      const rtn = songDetails.value.difficulties.filter((
          v: typeof songDetails.value.difficulties[0]
      ) => {
        let rNum: number = -1;
        switch (props.data.data.difficulty) {
          case "PAST":
            rNum = 0
            break
          case "PRESENT":
            rNum = 1
            break
          case "FUTURE":
            rNum = 2
            break
          case "BEYOND":
            rNum = 3
            break
        }
        return v.ratingClass === rNum
      })
      designer.value = rtn[0].chartDesigner
    })
  }
  dialog.value = true
}

const songDetails = ref<SongDetails>({} as SongDetails)

const designer = ref()
</script>

<template>
  <div @click="openDialog" class="card">
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
  <CustomDialog v-model:open="dialog">
    <div class="dialog_header_flex">
      <img class="dialog_image" :src="`http://localhost:61616/arcapi/v1/res/image?id=${props.data.data.id}&difficulty=${props.data.data.difficulty}`" alt="">
      <span>{{ data.name }}</span>
    </div>
    <div>详细信息:</div>
    <ul>
      <li>发布版本: {{ songDetails.version }}({{ new Date(songDetails.date * 1000).toDateString() }})</li>
      <li>BPM: {{ songDetails.bpm }}</li>
      <li>曲师: {{ songDetails.artist }}</li>
      <li>谱师: {{ designer }}</li>
    </ul>
  </CustomDialog>
</template>

<style scoped>
.dialog_image {
  width: 50px;
  height: 50px;
}

.dialog_header_flex {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.dialog_header_flex span {
  max-width: 50%;
  margin-left: 20px;
  font-size: 16px;
  color: #1f1e33;
}


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