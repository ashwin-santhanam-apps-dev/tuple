# com.aparigraha.tuple
Creates "Strongly Typed" Tuples with variable lengths in the compile time.

## Use case
In java different types are aggregated together only by classes.
Creating multiple small classes for small use cases might compromise simplicity. A collection of objects `Collection<Object>` can be used. But strong typing is compromised.
Tuples solve this by having static placeholders with generic types.

This library dynamically creates strongly typed tuples with the required number of placeholders in the compile time.
Since it's dynamic, if the usage of the Tuple is removed, the generated Tuple class is also removed. Keeping the code free of unused classes.

Tuples can be widely used in **Streams**, as it involves multiple transformations in different stages. And creating classes for each of those stage complicates the code.

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


## Stream support - Zip Streams
1. Each tuple class has a dynamically created static method `zip` that returns a Stream.
2. This is used to zip 'n' Streams `Stream<T0>, Stream<T1>, ... Stream<Tn>` into `Stream<Tuple(n)<T0, T1, ... Tn>>`.
3. This is non-terminal operation, hence the data will not be collected.
4. For the 'Tuple3' class, the 'zip' method takes 3 streams as input.
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