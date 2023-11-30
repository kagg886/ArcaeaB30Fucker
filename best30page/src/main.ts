import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import {init} from "./hook/nativeAPI.ts";
import {createPinia} from "pinia";
import piniaPersist from 'pinia-plugin-persistedstate'
init()
createApp(App).use(createPinia().use(piniaPersist)).mount('#app');