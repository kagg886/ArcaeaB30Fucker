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
    private final String id;
    //分数
    private final int score;
    //大P
    private final int shinyPerfectCount;
    //P
    private final int perfectCount;
    //Far
    private final int farCount;
    //Lost
    private final int lostCount;
    //难度
    private final Difficulty difficulty;
    //通关状态
    private final int clearStatus;

    //血量
    private final int health;



    public SingleSongData(String id, int score, int shinyPerfectCount, int perfectCount, int farCount, int lostCount,Difficulty difficulty, int clearStatus,int health) {
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

    public static class ClearStatus {
        //0为TL
        //1为普通C
        //2为FR
        //3为PM
        //4为简单C
        //5为困难C
        public static boolean isPM(SingleSongData data) {
            return data.clearStatus == 3;
        }

        public static boolean isFR(SingleSongData data) {
            return isPM(data) || data.clearStatus == 2;
        }

        public static boolean isTL(SingleSongData data) {
            return data.clearStatus == 0;
        }

        public static SingleSongData.ClearStatus.Difficulty getDiff(SingleSongData data) {
            return Difficulty.valueOf(data.clearStatus);
        }



        public enum Difficulty {
            EASY(4),NORMAL(1),HARD(5);
            private final int diff;

            Difficulty(int i) {
                this.diff = i;
            }

            public static Difficulty valueOf(int i) {
                return Arrays.stream(values()).filter((v) -> v.diff == i).findFirst().get();
            }
        }
    }

    public enum Difficulty {
        PAST(0),PRESENT(1),FUTURE(2),BEYOND(3);
        private final int diff;

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
