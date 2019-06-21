package com.physson.getphys;

/**
 * Created by olatunde on 8/30/17.
 */

public class Country {
    String name,code,dial_code;

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getName() {
        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Country(){

    }
}
