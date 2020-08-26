package com.power.doc.utils;

import java.util.function.BiConsumer;

public class Iterables {
    public static <E> void forEach(
            Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        if(elements==null||action==null) return;
        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }
}
