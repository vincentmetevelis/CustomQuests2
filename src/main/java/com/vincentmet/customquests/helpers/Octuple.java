package com.vincentmet.customquests.helpers;

public class Octuple<A, B, C, D, E, F, G, H>{
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;
    private F f;
    private G g;
    private H h;

    public Octuple(A _a, B _b, C _c, D _d, E _e, F _f, G _g, H _h){
        a = _a;
        b = _b;
        c = _c;
        d = _d;
        e = _e;
        f = _f;
        g = _g;
        h = _h;
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
    
    public H getEighth(){
        return h;
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
    
    public void setEighth(H h){
        this.h = h;
    }
    
    @Override
    public String toString() {
        return "A: " + a + " B: " + b + " C: " + c + " D: " + d + " E: " + e + " F: " + f + " G: " + g + " H: " + h;
    }
}
