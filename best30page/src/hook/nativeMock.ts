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
                window.native.onmessage?.(rtn)
            },500)
        }
    }
}