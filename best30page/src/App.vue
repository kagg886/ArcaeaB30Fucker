<script setup lang="ts">
import {Ref, ref} from "vue";
import {sendMessage} from "./hook/nativeAPI.ts";
import {UserProfile} from "./hook/type.ts";
import UserName from "./components/UserName.vue";

const divide = ref(20)
const user: Ref<UserProfile> = ref({} as UserProfile)

const imageBase = ref('')

// const myFix = (a: number) => {
//   let b = a.toString()
//   return b.substring(0, b.indexOf(".") === -1 ? b.length : b.indexOf(".") + 3)
// }

sendMessage('getUserProfile').then((res: UserProfile) => {
  if (res === {} as UserProfile) {
    sendMessage('rollback', '无法连接到注入到Arcaea的后端服务')
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

  sendMessage('assets', "img/rating_" + ratingType + ".png").then((call) => {
    imageBase.value = "data:image/jpeg;base64," + call
  })
}).catch((error: string) => {
  sendMessage('rollback', '无法连接到注入到Arcaea的后端服务:' + error)
})

</script>

<template>
  <div class="bg">
    <div class="profile">
      <user-name :user="user" v-model:imageBase="imageBase"></user-name>
      <div>
        ds
      </div>
    </div>
    <div class="container">
    </div>
  </div>
</template>

<style scoped>
.bg {
  width: 85%;
  height: 100%;

  display: flex;
  flex-direction: column;

}

.profile {
  border-bottom: 1px solid red;
  width: 100%;
  height: v-bind(divide+ '%');

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.container {
  width: 100%;
  height: v-bind(100 -divide+ '%');
}

</style>
