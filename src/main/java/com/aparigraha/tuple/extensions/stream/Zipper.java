package com.aparigraha.tuple.extensions.stream;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Zipper {
    public static Stream<List<Object>> zip(List<Stream<Object>> streams) {
        var spliterators = streams.stream().map(Stream::spliterator).toList();
        var size = spliterators.stream().map(Spliterator::getExactSizeIfKnown).min(Comparator.naturalOrder()).orElseThrow();
        var characteristics = spliterators.stream().map(Spliterator::characteristics)
                .reduce((c1, c2) -> c1 & c2 & (Spliterator.SIZED | Spliterator.ORDERED))
                .orElseThrow();

        var zipSplitIterator = new Spliterators.AbstractSpliterator<List<Object>>(size, characteristics) {
            @Override
            public boolean tryAdvance(Consumer<? super List<Object>> action) {
                Object[] container = new Object[spliterators.size()];
                Boolean[] found = new Boolean[spliterators.size()];

                IntStream.range(0, spliterators.size()).forEach(index -> {
                    spliterators.get(index).tryAdvance(element -> {
                        container[index] = element;
                        found[index] = true;
                    });
                });

                if (Arrays.stream(found).allMatch(Predicate.isEqual(true))) {
                    action.accept(Arrays.stream(container).toList());
                    return true;
                }
                else {
                    return false;
                }
            }
        };

        return StreamSupport.stream(zipSplitIterator, false);
    }
}
