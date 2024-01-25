<script setup lang="ts">
const open = defineModel('open', {
  default: false
})

const prevent = (e: Event) => {
  e.stopPropagation()
}
</script>

<template>
  <teleport to="body" v-if="open">
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
  top: 0;
  width: 100vw;
  height: 100vh;
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