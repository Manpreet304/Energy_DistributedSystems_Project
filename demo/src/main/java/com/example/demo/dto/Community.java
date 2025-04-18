package com.example.demo.dto;

import java.util.Date;

public class Community {
    private Type type;
    private double kwh;
    private Date datetime;

    public Community(double kwh, Date datetime) {
        this.type = Type.USER;
        this.kwh = kwh;
        this.datetime = datetime;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
