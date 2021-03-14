package com.vincentmet.customquests.helpers;

@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    /**
     * BiConsumer but with four arguments.
     */
    void accept(A a, B b, C c, D d);
}