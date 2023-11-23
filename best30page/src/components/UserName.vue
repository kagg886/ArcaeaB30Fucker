<script setup lang="ts">
import {UserProfile} from "../hook/type.ts";
import {computed, Ref} from "vue";

defineProps<{
  user: UserProfile
}>();

const imageBase: Ref<String | undefined> = defineModel('imageBase')

const base = computed(() => {
  return `url("${imageBase.value}")`
})
console.log(base.value)
</script>

<template>
  <div class="container">
    <div class="rating">
      <span class="ptt_real">{{ user.pttReal }}</span>
    </div>
    <div class="profile">
      <div class="name">{{ user.name }}</div>
      <div class="lite">B30: {{ user.pttB30 }}</div>
      <div class="lite">R10: {{ user.pttR10 }}</div>
      <div class="lite">MAX: {{ user.pttMax }}</div>
    </div>
  </div>


</template>

<style scoped>
.container {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.rating {
  width: 80px;
  height: 80px;
  background-image: v-bind(base);
  background-size: 100% 100%;

  display: flex;
  flex-direction: column-reverse;
  align-items: center;

  margin-bottom: -25px;

}

.ptt_real {
  color: white;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 18px;
  font-family: 'Kazesawa-Regular', serif;
}

.name {
  margin-top: 20px;
  font-size: 20px;
  font-family: 'ExoMedium', serif;
}

.profile {
  margin-top: 10px;
}

.lite {
  margin-top: 5px;
  font-size: 12px;
  font-family: 'ExoMedium', serif;
}

.lite:not(.lite ~ .lite) {
  margin-top: 10px;
}

</style>

