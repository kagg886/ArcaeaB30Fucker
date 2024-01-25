export interface UserProfile {
    name: string
    pttReal: number
    pttB30: number
    pttR10: number
    pttMax: number
}

export interface Best30Details {
    name: string
    ptt: number
    ex_diff: number
    data: SingleSongData
}

export interface SingleSongData {
    id: string
    score: number
    shinyPerfectCount: number
    perfectCount: number
    farCount: number
    lostCount: number
    difficulty: 'PAST' | 'PRESENT' | 'FUTURE' | 'BEYOND'
    clearStatus: number
    health: number
}

export interface Packet {
    type: string,
    data: any,
    id: number,
    callSuccess?: (data: string) => void
    callError?: (e: string) => void
}