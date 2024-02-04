import {Packet} from "./type.ts";

export const setAPIMockReturn = () => {
    window.native = {
        postMessage: (msg: string) => {
            const obj: Packet = JSON.parse(msg)
            console.log('mock to post:', obj)

            const rtn: Packet = {
                data: undefined,
                id: obj.id,
                type: obj.type
            }
            switch (obj.type) {
                case 'getUserProfile':
                    rtn.data = "{\"name\":\"kagg886\",\"pttB30\":11.707333333333334,\"pttMax\":11.77325,\"pttR10\":11.838000000000001,\"pttReal\":11.74}"
                    break
                case 'songInfo':
                    rtn.data = "{\n" +
                        "        \"idx\": 57,\n" +
                        "        \"id\": \"dreaminattraction\",\n" +
                        "        \"title_localized\": {\n" +
                        "            \"en\": \"Dreamin' Attraction!!\"\n" +
                        "        },\n" +
                        "        \"artist\": \"翡乃イスカ\",\n" +
                        "        \"search_title\": {\n" +
                        "            \"ja\": [\n" +
                        "                \"どりーみんあとらくしょん\"\n" +
                        "            ],\n" +
                        "            \"ko\": [\n" +
                        "                \"드리밍 어트랙션\"\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        \"search_artist\": {\n" +
                        "            \"en\": [\n" +
                        "                \"hino isuka\"\n" +
                        "            ],\n" +
                        "            \"ja\": [\n" +
                        "                \"ひのいすか\"\n" +
                        "            ],\n" +
                        "            \"ko\": [\n" +
                        "                \"히노 이스카\"\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        \"bpm\": \"205\",\n" +
                        "        \"bpm_base\": 205,\n" +
                        "        \"set\": \"base\",\n" +
                        "        \"purchase\": \"\",\n" +
                        "        \"audioPreview\": 39804,\n" +
                        "        \"audioPreviewEnd\": 60878,\n" +
                        "        \"side\": 0,\n" +
                        "        \"world_unlock\": true,\n" +
                        "        \"bg\": \"base_light\",\n" +
                        "        \"bg_inverse\": \"base_conflict\",\n" +
                        "        \"date\": 1509667201,\n" +
                        "        \"version\": \"1.5\",\n" +
                        "        \"difficulties\": [\n" +
                        "            {\n" +
                        "                \"ratingClass\": 0,\n" +
                        "                \"chartDesigner\": \"Nitro\",\n" +
                        "                \"jacketDesigner\": \"\",\n" +
                        "                \"rating\": 4\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"ratingClass\": 1,\n" +
                        "                \"chartDesigner\": \"Nitro\",\n" +
                        "                \"jacketDesigner\": \"\",\n" +
                        "                \"rating\": 7\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"ratingClass\": 2,\n" +
                        "                \"chartDesigner\": \"Nitro\",\n" +
                        "                \"jacketDesigner\": \"\",\n" +
                        "                \"rating\": 9\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }"
                    break
                case 'b30':
                    rtn.data = "[\n" +
                        "    {\n" +
                        "        \"data\": {\n" +
                        "            \"clearStatus\": 2,\n" +
                        "            \"difficulty\": \"FUTURE\",\n" +
                        "            \"farCount\": 10,\n" +
                        "            \"health\": 100,\n" +
                        "            \"id\": \"cyaegha\",\n" +
                        "            \"lostCount\": 8,\n" +
                        "            \"perfectCount\": 1350,\n" +
                        "            \"score\": 9906210,\n" +
                        "            \"shinyPerfectCount\": 1240\n" +
                        "        },\n" +
                        "        \"name\": \"Cyaegha\",\n" +
                        "        \"ptt\": 12.23\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": {\n" +
                        "            \"clearStatus\": 5,\n" +
                        "            \"difficulty\": \"FUTURE\",\n" +
                        "            \"farCount\": 16,\n" +
                        "            \"health\": 100,\n" +
                        "            \"id\": \"singularity\",\n" +
                        "            \"lostCount\": 4,\n" +
                        "            \"perfectCount\": 1085,\n" +
                        "            \"score\": 9892336,\n" +
                        "            \"shinyPerfectCount\": 934\n" +
                        "        },\n" +
                        "        \"name\": \"Singularity\",\n" +
                        "        \"ptt\": 12.16\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": {\n" +
                        "            \"clearStatus\": 5,\n" +
                        "            \"difficulty\": \"FUTURE\",\n" +
                        "            \"farCount\": 25,\n" +
                        "            \"health\": 100,\n" +
                        "            \"id\": \"gou\",\n" +
                        "            \"lostCount\": 11,\n" +
                        "            \"perfectCount\": 1486,\n" +
                        "            \"score\": 9846934,\n" +
                        "            \"shinyPerfectCount\": 1337\n" +
                        "        },\n" +
                        "        \"name\": \"Misdeed -la bonté de Dieu et l'origine du mal-\",\n" +
                        "        \"ptt\": 12.13\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": {\n" +
                        "            \"clearStatus\": 5,\n" +
                        "            \"difficulty\": \"FUTURE\",\n" +
                        "            \"farCount\": 18,\n" +
                        "            \"health\": 100,\n" +
                        "            \"id\": \"lastcelebration\",\n" +
                        "            \"lostCount\": 4,\n" +
                        "            \"perfectCount\": 1453,\n" +
                        "            \"score\": 9913219,\n" +
                        "            \"shinyPerfectCount\": 1355\n" +
                        "        },\n" +
                        "        \"name\": \"Last Celebration\",\n" +
                        "        \"ptt\": 12.07\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": {\n" +
                        "            \"clearStatus\": 5,\n" +
                        "            \"difficulty\": \"FUTURE\",\n" +
                        "            \"farCount\": 22,\n" +
                        "            \"health\": 100,\n" +
                        "            \"id\": \"ringedgenesis\",\n" +
                        "            \"lostCount\": 8,\n" +
                        "            \"perfectCount\": 1116,\n" +
                        "            \"score\": 9835122,\n" +
                        "            \"shinyPerfectCount\": 917\n" +
                        "        },\n" +
                        "        \"name\": \"Ringed Genesis\",\n" +
                        "        \"ptt\": 11.98\n" +
                        "    }\n" +
                        "]"
                    break

                case 'rollback':
                    console.log('rollback!',obj)
                    break

                default:
                    throw new Error('No Supported cmd:' + obj.type.toString())
            }
            setTimeout(() => {
                window.native.onmessage?.({
                    data: JSON.stringify(rtn)
                })
            },500)
        }
    }
}