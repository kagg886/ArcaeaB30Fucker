import {defineStore} from "pinia";
import {ref} from "vue";

export const charList = [
    ...range(0,73),
    '99',

    ...(range(0,2).map((v=>v+'u'))), //'0u','1u','2u',
    '4u', '10u','11u','12u','13u',
    '19u','21u',
    ...(range(26,29).map((v=>v+'u'))), //'26u','27u','28u','29u',
    '36u','42u','43u','66u','73u'
]

export const useCharacter = defineStore('character', () => {
    const id = ref(7)

    function getURL() {
        return `http://localhost:61616/arcapi/v1/res/assets?path=char/1080/${charList[id.value]}.png`
    }

    return {
        id,
        getURL
    }
}, {
    persist: true
})

function range(l: number, r: number): string[] {
    let a:string[] = [];
    for (let i = l; i <= r; i++) {
        a.push(i.toString(10))
    }
    return a;
}