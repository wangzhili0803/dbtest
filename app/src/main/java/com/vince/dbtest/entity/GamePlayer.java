package com.vince.dbtest.entity;

/**
 * Created by Administrator on 2015/11/15.
 */
public class GamePlayer {
    private int id;
    private String name;
    private int score;
    private int level;

    public GamePlayer() {
    }

    public GamePlayer(String name, int score, int level) {
        this.name = name;
        this.score = score;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", level=" + level +
                '}';
    }


}
