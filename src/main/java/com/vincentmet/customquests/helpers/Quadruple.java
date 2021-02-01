package com.vincentmet.customquests.helpers;

public class Quadruple<A, B, C, D>{
    private A a;
    private B b;
    private C c;
    private D d;

    public Quadruple(A _a, B _b, C _c, D _d){
        a = _a;
        b = _b;
        c = _c;
        d = _d;
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

    @Override
    public String toString() {
        return "A: " + a + " B: " + b + " C: " + c + " D: " + d;
    }
}
