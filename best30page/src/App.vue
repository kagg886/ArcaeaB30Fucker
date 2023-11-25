<script setup lang="ts">
import {Ref, ref} from "vue";
import {useNativeAPI} from "./hook/nativeAPI.ts";
import {UserProfile} from "./hook/type.ts";
import UserName from "./components/UserName.vue";
import Best30 from "./components/Best30List.vue";
import ProgressBar from "./components/ProgressBar.vue";

const divide = ref(20)
const user: Ref<UserProfile> = ref({} as UserProfile)

const rating = ref('')
useNativeAPI('getUserProfile').then((res: UserProfile) => {
  if (res === {} as UserProfile) {
    useNativeAPI('rollback', '无法连接到注入到Arcaea的后端服务')
    return
  }
  user.value = res

  user.value.pttB30 = Number(user.value.pttB30.toFixed(2))
  user.value.pttR10 = Number(user.value.pttR10.toFixed(2))
  user.value.pttMax = Number(user.value.pttMax.toFixed(2))

  let ratingType = 0, b30Avt = user.value.pttReal
  if (b30Avt > 13.00) {
    ratingType = 7;
  } else if (b30Avt > 12.50) {
    ratingType = 6;
  } else if (b30Avt > 12.00) {
    ratingType = 5;
  } else if (b30Avt > 11.00) {
    ratingType = 4;
  } else if (b30Avt > 10.00) {
    ratingType = 3;
  } else if (b30Avt > 7.00) {
    ratingType = 2;
  } else if (b30Avt > 3.50) {
    ratingType = 1;
  } else {
    ratingType = 0;
  }

  rating.value = "http://localhost:61616/arcapi/v1/res/assets?path=img/rating_" + ratingType + ".png"
}).catch((error: string) => {
  useNativeAPI('rollback', '无法连接到注入到Arcaea的后端服务:' + error)
})

</script>

<template>
  <div class="bg">
    <div class="profile">
      <user-name :user="user" v-model:rating="rating"></user-name>
      <div>
        <img :src="'http://localhost:61616/arcapi/v1/res/assets?path=char/1080/7.png'" alt="" width="100px" height="100px">
      </div>
    </div>
    <div class="container">
      <Suspense>
        <Best30/>
        <template #fallback>
          <div class="loading">
            <progress-bar/>
          </div>
        </template>
      </Suspense>
    </div>
  </div>
</template>

<style scoped>
.bg {
  width: 85%;
  height: 100%;

  display: flex;
  flex-direction: column;
  background-image: url("../assets/bg.png");
  background-repeat: no-repeat;
  background-size: 100% 100%;
  background-attachment: fixed
}

.profile {
  width: 100%;
  height: v-bind(divide+ '%');

  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.container {
  width: 100%;
  height: v-bind(100 -divide+ '%');
}

</style>
