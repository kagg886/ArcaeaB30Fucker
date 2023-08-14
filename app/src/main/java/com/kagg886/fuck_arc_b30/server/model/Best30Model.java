package com.kagg886.fuck_arc_b30.server.model;

/**
 * B30列表
 *
 * @author kagg886
 * @date 2023/8/14 9:15
 **/
public class Best30Model {
    private final String name;
    private final SingleSongData data;
    private final double ptt;

    public Best30Model(String name, SingleSongData data, double ptt) {
        this.name = name;
        this.data = data;
        this.ptt = ptt;
    }

    public String getName() {
        return name;
    }

    public SingleSongData getData() {
        return data;
    }

    public double getPtt() {
        return ptt;
    }
}
