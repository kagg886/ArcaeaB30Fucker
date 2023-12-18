import {defineStore} from "pinia";
import {ref} from "vue";

export const usePromptSetting = defineStore('prompt',() => {
    const visibleName = ref(true)
    const visiblePtt = ref(true)
    return {
        visibleName,
        visiblePtt
    }
},{
    persist: true
})