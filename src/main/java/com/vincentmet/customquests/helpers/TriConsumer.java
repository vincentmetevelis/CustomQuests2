package com.vincentmet.customquests.helpers;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    /**
     * BiConsumer but with three arguments.
     */
    void accept(A a, B b, C c);
}