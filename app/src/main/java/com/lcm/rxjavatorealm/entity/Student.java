package com.lcm.rxjavatorealm.entity;

import io.realm.RealmObject;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2018/5/24 20:09
 * Desc:
 * *****************************************************************
 */
public class Student extends RealmObject {

    private int id;
    private String nanme;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNanme() {
        return nanme;
    }

    public void setNanme(String nanme) {
        this.nanme = nanme;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", nanme='" + nanme + '\'' +
                ", age=" + age +
                '}';
    }
}
