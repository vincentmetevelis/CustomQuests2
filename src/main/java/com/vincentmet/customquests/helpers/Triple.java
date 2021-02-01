package com.vincentmet.customquests.helpers;

public class Triple<L, M, R>{
    private L l;
    private M m;
    private R r;

    public Triple(L _l, M _m, R _r){
        l = _l;
        m = _m;
        r = _r;
    }

    public L getLeft() {
        return l;
    }

    public M getMiddle() {
        return m;
    }

    public R getRight() {
        return r;
    }
    
    public void setL(L l){
        this.l = l;
    }
    
    public void setM(M m){
        this.m = m;
    }
    
    public void setR(R r){
        this.r = r;
    }
    
    public void copyFrom(Triple<L, M, R> og){
        this.setL(og.getLeft());
        this.setM(og.getMiddle());
        this.setR(og.getRight());
    }
    
    @Override
    public String toString() {
        return "L: " + l + " M: " + m + " R: " + r;
    }
}
