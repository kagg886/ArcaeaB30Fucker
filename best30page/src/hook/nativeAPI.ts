import {setAPIMockReturn} from "./nativeMock.ts";
import {Packet} from "./type.ts";

let callbacks: Array<Packet> = []


//usage:
// const getProfile = () => {
//   sendMessage('getUserProfile').then((res: UserProfile) => {
//     console.log('success', res)
//   }).catch((error: string) => {
//     console.error('error:', error)
//   })
// }
export const init = () => {
    if (window.native === undefined) {
        setAPIMockReturn()
    }


    window.native.onmessage = (ev: Omit<Packet, 'callSuccess'>) => {
        ev = JSON.parse(ev["data"])
        let id = ev.id
        try {
            ev.data = JSON.parse(ev.data)
        } catch (ignored) {
        }
        console.log('[Client]recv->', JSON.stringify(ev))
        callbacks = callbacks.filter((value) => {
            if (value.id === id) {
                if (value.callError !== undefined && ev.type === 'error') {
                    value.callError(ev.data)
                } else if (value.callSuccess !== undefined) {
                    value.callSuccess(ev.data)
                }
                return false
            }
            return true
        })
    }
}

export const useNativeAPI: (type: string, data?: any) => Promise<any> = (type: string, data: any = {}) => {
    const id = Math.floor(Math.random() * 1000000)
    const packet: Packet = {
        type,
        data: typeof data === "string" ? data : JSON.stringify(data),
        id
    }
    callbacks.push(packet)
    let p = new Promise((resolve, reject) => {
        packet.callSuccess = resolve
        packet.callError = reject
    })
    console.log('[Client]send->', JSON.stringify(packet))
    window.native.postMessage(JSON.stringify(packet))
    return p;
}

