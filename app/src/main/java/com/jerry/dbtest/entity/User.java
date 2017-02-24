package com.jerry.dbtest.entity;

/**
 * Created by wzl-pc on 2017/2/20.
 */
public class User {
    private int id;
    private String name;
    private String age;
    private String blog;

    public User(){}

    public User(int id, String name, String age, String blog) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", blog='" + blog + '\'' +
                '}';
    }
}
