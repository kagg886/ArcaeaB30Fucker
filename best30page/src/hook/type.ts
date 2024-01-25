export type UserProfile = {
    name: string
    pttReal: number
    pttB30: number
    pttR10: number
    pttMax: number
}

export type Best30Details = {
    name: string
    ptt: number
    ex_diff: number
    data: SingleSongData
}

export type SingleSongData = {
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

export type Packet = {
    type: string,
    data: any,
    id: number,
    callSuccess?: (data: string) => void
    callError?: (e: string) => void
}

//{
//         "idx": 57,
//         "id": "dreaminattraction",
//         "title_localized": {
//             "en": "Dreamin' Attraction!!"
//         },
//         "artist": "翡乃イスカ",
//         "search_title": {
//             "ja": [
//                 "どりーみんあとらくしょん"
//             ],
//             "ko": [
//                 "드리밍 어트랙션"
//             ]
//         },
//         "search_artist": {
//             "en": [
//                 "hino isuka"
//             ],
//             "ja": [
//                 "ひのいすか"
//             ],
//             "ko": [
//                 "히노 이스카"
//             ]
//         },
//         "bpm": "205",
//         "bpm_base": 205,
//         "set": "base",
//         "purchase": "",
//         "audioPreview": 39804,
//         "audioPreviewEnd": 60878,
//         "side": 0,
//         "world_unlock": true,
//         "bg": "base_light",
//         "bg_inverse": "base_conflict",
//         "date": 1509667201,
//         "version": "1.5",
//         "difficulties": [
//             {
//                 "ratingClass": 0,
//                 "chartDesigner": "Nitro",
//                 "jacketDesigner": "",
//                 "rating": 4
//             },
//             {
//                 "ratingClass": 1,
//                 "chartDesigner": "Nitro",
//                 "jacketDesigner": "",
//                 "rating": 7
//             },
//             {
//                 "ratingClass": 2,
//                 "chartDesigner": "Nitro",
//                 "jacketDesigner": "",
//                 "rating": 9
//             }
//         ]
//     }
export type SongDetails = {
    title_localized: {
        en: string
    }
    artist: string
    bpm: number
    date: number
    version: string
    difficulties: Array<{
        chartDesigner: string,
        rating: number
        ratingClass: number
    }>
}