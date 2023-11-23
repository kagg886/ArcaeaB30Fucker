import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import {init} from "./hook/nativeAPI.ts";

init()
document.body.style.height = window.innerHeight + 'px'
document.body.style.width = window.innerWidth + 'px'
createApp(App).mount('#app');