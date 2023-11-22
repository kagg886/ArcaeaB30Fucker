const API = window.native;

let callbacks: Array<Packet> = []

export const init = () => {
    window.native.onmessage = (ev: Omit<Packet, 'callSuccess'>) => {
        ev = JSON.parse(ev.data)
        ev.data = JSON.parse(ev.data)
        console.log('[Client]recv->',JSON.stringify(ev))
        callbacks =  callbacks.filter((value) => {
            if (value.id === ev.id) {
                if (value.callSuccess !== undefined) {
                    value.callSuccess(ev.data)
                }
                return false
            }
            return true
        })
    }
}

export const sendMessage = (type: string, data: any) => {
    const id = Math.floor(Math.random() * 1000000)
    const packet: Packet = {
        type,
        data: JSON.stringify(data),
        id
    }
    callbacks.push(packet)
    let p = new Promise((resolve) => {
        packet.callSuccess = resolve
    })
    console.log('[Client]send->',JSON.stringify(packet))
    API.postMessage(JSON.stringify(packet))
    return p;
}

interface Packet {
    type: string,
    data: any,
    id: number,
    callSuccess?: (data: string) => void
}