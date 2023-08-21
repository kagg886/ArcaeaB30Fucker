package com.kagg886.fuck_arc_b30.server.model;

/**
 * @author kagg886
 * @date 2023/8/21 16:50
 **/
public class UserProfile {
    private String name;
    private double pttReal;
    private double pttB30;
    private double pttR10;

    public void setName(String name) {
        this.name = name;
    }

    public void setPttReal(double pttReal) {
        this.pttReal = pttReal;
    }

    public void setPttB30(double pttB30) {
        this.pttB30 = pttB30;
    }

    public void setPttR10(double pttR10) {
        this.pttR10 = pttR10;
    }

    public String getName() {
        return name;
    }

    public double getPttReal() {
        return pttReal;
    }

    public double getPttB30() {
        return pttB30;
    }

    public double getPttR10() {
        return pttR10;
    }

    public UserProfile(String name, double pttReal, double pttB30, double pttR10) {
        this.name = name;
        this.pttReal = pttReal;
        this.pttB30 = pttB30;
        this.pttR10 = pttR10;
    }
}
