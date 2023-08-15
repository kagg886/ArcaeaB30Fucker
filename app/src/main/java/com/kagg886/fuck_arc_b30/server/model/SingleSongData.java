package com.kagg886.fuck_arc_b30.server.model;

import java.util.Arrays;

/**
 * 单曲数据
 *
 * @author kagg886
 * @date 2023/8/13 16:51
 **/
public class SingleSongData {
    //id
    private String id;
    //分数
    private int score;
    //大P
    private int shinyPerfectCount;
    //P
    private int perfectCount;
    //Far
    private int farCount;
    //Lost
    private int lostCount;
    //难度
    private Difficulty difficulty;
    //通关状态
    private int clearStatus;

    //血量
    private int health;

    public void setId(String id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setShinyPerfectCount(int shinyPerfectCount) {
        this.shinyPerfectCount = shinyPerfectCount;
    }

    public void setPerfectCount(int perfectCount) {
        this.perfectCount = perfectCount;
    }

    public void setFarCount(int farCount) {
        this.farCount = farCount;
    }

    public void setLostCount(int lostCount) {
        this.lostCount = lostCount;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setClearStatus(int clearStatus) {
        this.clearStatus = clearStatus;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public SingleSongData(String id, int score, int shinyPerfectCount, int perfectCount, int farCount, int lostCount, Difficulty difficulty, int clearStatus, int health) {
        this.id = id;
        this.score = score;
        this.shinyPerfectCount = shinyPerfectCount;
        this.perfectCount = perfectCount;
        this.farCount = farCount;
        this.lostCount = lostCount;
        this.difficulty = difficulty;
        this.clearStatus = clearStatus;
        this.health = health;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getShinyPerfectCount() {
        return shinyPerfectCount;
    }

    public int getPerfectCount() {
        return perfectCount;
    }

    public int getFarCount() {
        return farCount;
    }

    public int getLostCount() {
        return lostCount;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getClearStatus() {
        return clearStatus;
    }

    public int getHealth() {
        return health;
    }

    public enum Difficulty {
        PAST(0),PRESENT(1),FUTURE(2),BEYOND(3);
        private int diff;

        Difficulty(int i) {
            this.diff = i;
        }

        public int getDiff() {
            return diff;
        }

        public static Difficulty valueOf(int i) {
            return Arrays.stream(values()).filter((v) -> v.diff == i).findFirst().get();
        }
    }

    @Override
    public String toString() {
        return "SingleSongData{" + "id='" + id + '\'' +
                ", score=" + score +
                ", shinyPerfectCount=" + shinyPerfectCount +
                ", perfectCount=" + perfectCount +
                ", farCount=" + farCount +
                ", lostCount=" + lostCount +
                ", difficulty=" + difficulty +
                ", clearStatus=" + clearStatus +
                ", health=" + health +
                '}';
    }
}
