import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import {init} from "./hook/nativeAPI.ts";

init()
createApp(App).mount('#app')
