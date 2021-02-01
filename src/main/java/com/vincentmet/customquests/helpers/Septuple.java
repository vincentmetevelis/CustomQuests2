package com.vincentmet.customquests.helpers;

public class Septuple<A, B, C, D, E, F, G>{
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;
    private F f;
    private G g;

    public Septuple(A _a, B _b, C _c, D _d, E _e, F _f, G _g){
        a = _a;
        b = _b;
        c = _c;
        d = _d;
        e = _e;
        f = _f;
        g = _g;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    public C getThird() {
        return c;
    }

    public D getFourth() {
        return d;
    }
    
    public E getFifth() {
        return e;
    }
    
    public F getSixth() {
        return f;
    }
    
    public G getSeventh(){
        return g;
    }
    
    public void setFirst(A a){
        this.a = a;
    }
    
    public void setSecond(B b){
        this.b = b;
    }
    
    public void setThird(C c){
        this.c = c;
    }
    
    public void setFourth(D d){
        this.d = d;
    }
    
    public void setFifth(E e){
        this.e = e;
    }
    
    public void setSixth(F f){
        this.f = f;
    }
    
    public void setSeventh(G g){
        this.g = g;
    }
    
    @Override
    public String toString() {
        return "A: " + a + " B: " + b + " C: " + c + " D: " + d + " E: " + e + " F: " + f + " G: " + g;
    }
}
