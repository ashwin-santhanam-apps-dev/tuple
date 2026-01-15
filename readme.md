# com.aparigraha.tuple
Create "Strongly Typed" Tuples with variable lengths in the compile time.

## Basic Usage
1. Add the annotation `@TupleSpec` on top of a class or a method.
2. The value for the annotations is an array of integers, each corresponding to a tuple class's field count.
3. Each item in the `@TupleSpec` annotation creates a Tuple record.
4. **Compile the java project** to create the respective classes.
5. These classes are created in the package `com.aparigraha.tuples`.
### Example
`@TupleSpec({2, 3})` creates two Tuple records, `Tuple2` and `Tuple3`.
The dynamically generated class for `Tuple2` will be similar to the one below.
```java
public record Tuple2<T0, T1> (
   T0 item0,
   T1 item1
) {}
```
Usage example of `Tuple3`
```java
var tuple3 = new Tuple3<>("Alice", 28, "Wonderland");
String name = tuple3.item0();
Integer age = tuple3.item1();
String city = tuple3.item2();
```
   Hence, no type casting is required and all the elements are strongly typed.


## Stream support
1. Each tuple class has a static method `zip` that returns a Stream.
2. This is used to zip `n` Streams and return a `Stream<Tuple<T0, T1, ... Tn>>`.
3. This is non-terminal operation, hence the data will not be collected.
### Example
The dynamically generated class for `Tuple2` will be similar to the one below.
```java
public record Tuple2<T0, T1>(
    T0 item0,
    T1 item1
) {
    public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> t1) {
        // Code that zips tuples without collecting            
    }
}
```
To use the zip, simply pass the individual streams.
```java
Stream<Integer> integerStream = Stream.of(1, 2, 3);
Stream<String> stringStream = Stream.of("A", "B", "C");

var tuple2Stream = Tuple2.zip(integerStream, stringStream);
var listResult = tuple2Stream
        .map(tuple2 -> 
                new Tuple2(
                        tuple2.item0() + 1, 
                        tuple2.item1() + "X"
                )
        ).toList();
```