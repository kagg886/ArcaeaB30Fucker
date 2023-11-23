package com.kagg886.fuck_arc_b30.app.ui.dashboard.js;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * @Description
 * @Author kagg886
 * @Date 2023/11/22 上午10:52
 */
public class JSPacket {
    private String type;
    private String data;
    private Integer id;

    public JSONObject decodeData() {
        return JSON.parseObject(data);
    }

    public JSPacket(String type, String data, Integer id) {
        this.type = type;
        this.data = data;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JSPacket asReply(Object data) {
        return new JSPacket(type, data.getClass() == String.class ? data.toString() : JSON.toJSONString(data), id);
    }

    public JSPacket asError(String cause) {
        return new JSPacket("error", cause, id);
    }
}
