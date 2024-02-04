<script setup lang="ts">
import {onMounted, onUnmounted, ref} from "vue";

const open = defineModel('open', {
  default: false
})

const pageHeadOffset = ref(0)
const scrollListener = () => {
  pageHeadOffset.value = document.documentElement.scrollTop;
}

onMounted(() => {
  document.addEventListener('scroll',scrollListener)
})

onUnmounted(() => {
  document.removeEventListener('scroll',scrollListener);
})

//Android端不允许使用100vw设置
const screenHeight = ref(document.documentElement.clientHeight)
const prevent = (e: Event) => {
  e.stopPropagation()
}
</script>

<template>
  <teleport to="#app" v-if="open">
    <div class="bg" @click="open = false">
      <div class="bg_container" @click="prevent">
        <div class="slots">
          <slot>

          </slot>
        </div>
      </div>
    </div>
  </teleport>
</template>

<style scoped>

.bg {
  position: absolute;
  left: 0;
  top: v-bind(pageHeadOffset + 'px');
  width: 100vw;
  height: v-bind(screenHeight + 'px');
  z-index: 100;
  background-color: rgba(128, 128, 128, 0.3);
}


.bg_container {
  background-color: white;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
  border-radius: 10px;
}

.slots {
  padding: 10px;
}
</style>