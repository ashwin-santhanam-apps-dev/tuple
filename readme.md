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
```java
@SpringBootApplication
@TupleSpec({2, 3})
public class FitnessTrackerApiApplication {}
```
The dynamically generated class for `Tuple2` will be similar to the one below.
```java
public record Tuple2<T0, T1> (
   T0 item0,
   T1 item1
) {}
```
Usage example of `Tuple2`
```java
var tuple2 = new Tuple2<>("Alice", 28);
String name = tuple2.item0();
Integer age = tuple2.item1();
```
   Hence, no type casting is required and all the elements are strongly typed.


## Stream support
1. Each tuple class has a static method `zip` that returns a Stream.
2. This is used to zip `n` Streams and return a `Stream<Tuple<T0, T1, ... Tn>>`.
3. This is non-terminal operation, hence the data will not be collected.
The dynamically generated class for `Tuple3` will be similar to the one below.
```java
public record Tuple3<T0, T1, T2>(
    T0 item0,
    T1 item1,
    T2 item2
) {
    public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {
        // Code that zips tuples without collecting            
    }
}
```
### Example
Let's assume a case where there are three streams, each representing a field for employees.
1. Id - Integer Stream
2. Name - String Stream
3. FullTimeEmployment - Boolean Stream
```java
Stream<Integer> idStream = Stream.of(1, 2, 3);
Stream<String> nameStream = Stream.of("Alice", "Bob", "Carla");
Stream<Boolean> fullTimeEmploymentStream = Stream.of(true, false, true);
```
To use the zip, simply pass the individual streams to the respective `zip` method of the tuple class.
```java
Stream<Tuple3<Integer, String, Boolean>> employeeStream = Tuple3.zip(idStream, nameStream, fullTimeEmploymentStream);
Stream<Integer> fullTimeEmployeeIds = employeeStream.filter(Tuple3::item2).map(x -> x.item0());
```
This zips the three individual streams into a single stream. The length of the stream will be the length of the smallest stream.
The `zip` method does not collect values internally. It's a intermediate operation. 