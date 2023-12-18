<script setup lang="ts">
import {UserProfile} from "../hook/type.ts";
import {computed, Ref} from "vue";
import {usePromptSetting} from "../store/prompt-store.ts";

defineProps<{
  user: UserProfile
}>();

const rating: Ref<String | undefined> = defineModel('rating')
const setting = usePromptSetting()
const base = computed(() => {
  return setting.visiblePtt ? `url("${rating.value}")` : `url("http://localhost:61616/arcapi/v1/res/assets?path=img/rating_off.png")`
})

console.log(base.value)
</script>

<template>
  <div class="container">
    <div class="rating">
      <span v-if="setting.visiblePtt" @click="setting.visiblePtt = !setting.visiblePtt" class="ptt_real">{{ user.pttReal }}</span>
      <span v-else @click="setting.visiblePtt = !setting.visiblePtt" class="ptt_real">--.--</span>
    </div>
    <div class="profile">
      <div @click="setting.visibleName = !setting.visibleName" class="name" v-if="setting.visibleName">{{ user.name }}</div>
      <div @click="setting.visibleName = !setting.visibleName" class="name" v-else>----------</div>


      <div @click="setting.visiblePtt = !setting.visiblePtt" v-if="setting.visiblePtt" class="lite">B30: {{ user.pttB30 }}</div>
      <div @click="setting.visiblePtt = !setting.visiblePtt" v-else class="lite">B30: --.--</div>

      <div @click="setting.visiblePtt = !setting.visiblePtt" v-if="setting.visiblePtt" class="lite">R10: {{ user.pttR10 }}</div>
      <div @click="setting.visiblePtt = !setting.visiblePtt" v-else class="lite">R10: --.--</div>

      <div @click="setting.visiblePtt = !setting.visiblePtt" v-if="setting.visiblePtt" class="lite">MAX: {{ user.pttMax }}</div>
      <div @click="setting.visiblePtt = !setting.visiblePtt" v-else class="lite">MAX: --.--</div>
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

