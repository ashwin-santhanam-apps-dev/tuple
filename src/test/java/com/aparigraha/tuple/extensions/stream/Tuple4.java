package com.aparigraha.tuple.extensions.stream;

import java.util.List;
import java.util.stream.Stream;


import com.aparigraha.tuple.extensions.stream.Zipper;


public record Tuple4<T0, T1, T2, T3> (
        T0 item0,
        T1 item1,
        T2 item2,
        T3 item3
) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple4<?, ?, ?, ?> that) {
            return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2 && this.item3 == that.item3;
        } else return false;
    }


    public static <T0, T1, T2, T3> Stream<Tuple4<T0, T1, T2, T3>> zip(
            Stream<T0> stream0,
            Stream<T1> stream1,
            Stream<T2> stream2,
            Stream<T3> stream3
    ) {
        List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2, (Stream<Object>) stream3);
        return Zipper.zip(streams).map(zipped -> new Tuple4<>(
                (T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2), (T3) zipped.get(3)
        ));
    }
}