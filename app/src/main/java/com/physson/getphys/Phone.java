package com.physson.getphys;

/**
 * Created by olatunde on 7/17/2017.
 */

public class Phone {
    private String name;
    private String phones;
    private int phonecount;
    public Phone(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public int getPhonecount() {
        return phonecount;
    }

    public void setPhonecount(int phonecount) {
        this.phonecount = phonecount;
    }
    @Override
    public String toString(){
        return getPhones()+" "+getName();
    }
}
