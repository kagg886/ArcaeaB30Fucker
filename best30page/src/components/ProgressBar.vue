<script setup lang="ts">

</script>

<template>
  <div class="box">
    <span></span>
    <span></span>
    <span></span>
    <span></span>
    <span></span>
  </div>
</template>

<style scoped>

.box {
  position: relative;
  width: 100px;
  height: 50px;
  margin: 50px auto;
}

.box span {
  position: absolute;
  width: 20px;
  height: 20px;
  background: #3498db;
  opacity: 0.5;
  border-radius: 100%;
  animation: anim 1s infinite ease-in-out;
}

.box > :nth-child(2) {
  left: 20px;
  animation-delay: 0.2s;
}

.box > :nth-child(3) {
  left: 40px;
  animation-delay: 0.4s;
}

.box > :nth-child(4) {
  left: 60px;
  animation-delay: 0.6s;
}

.box > :nth-child(5) {
  left: 80px;
  animation-delay: 0.8s;
}

@keyframes anim {
  0% {
    opacity: 0.3;
    transform: translateY(0px);
  }
  50% {
    opacity: 1;
    transform: translateY(-10px);
    background: #f9cdff;
  }
  100% {
    opacity: 0.3;
    transform: translateY(0px);
  }
}

</style>