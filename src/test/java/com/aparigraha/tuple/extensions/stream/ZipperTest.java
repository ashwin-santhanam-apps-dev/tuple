package com.aparigraha.tuple.extensions.stream;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ZipperTest {
    @Test
    void shouldZipTheGivenStreams() {
        var zipped = Zipper.zip(List.of(
                Stream.of(1, 2, 3, 4),
                Stream.of(11, 12, 13),
                Stream.of(10, 9, 8)
        ));

        var collected = zipped.toList();

        assertEquals(1, collected.get(0).get(0));
        assertEquals(11, collected.get(0).get(1));
        assertEquals(10, collected.get(0).get(2));
        assertEquals(3, collected.get(0).size());

        assertEquals(2, collected.get(1).get(0));
        assertEquals(12, collected.get(1).get(1));
        assertEquals(9, collected.get(1).get(2));
        assertEquals(3, collected.get(1).size());

        assertEquals(3, collected.get(2).get(0));
        assertEquals(13, collected.get(2).get(1));
        assertEquals(8, collected.get(2).get(2));
        assertEquals(3, collected.get(2).size());

        assertEquals(3, collected.size());
    }


    @Test
    void shouldZipWithTypeSafeWrapper() {
        var ids =  Stream.of(0, 1, 2, 3);
        var names = Stream.of("A", "B", "C");
        var ids1 =  Stream.of(0, 1, 2, 3);
        var names1 = Stream.of("A", "B", "C");

        var zipped = Tuple4.zip(ids, names, ids1, names1).toList();

        assertEquals(new Tuple4<>(0, "A", 0, "A"), zipped.get(0));
        assertEquals(new Tuple4<>(1, "B", 1, "B"), zipped.get(1));
        assertEquals(new Tuple4<>(2, "C", 2, "C"), zipped.get(2));
        assertEquals(3, zipped.size());
    }
}