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
### Numbered Tuple
1. A static method `Object DynamicTuple.of(Object... args)` is given by the library initially.
```java
public class DynamicTuple {
    public static Object of(Object... args) {}
}
```
2. If a statement `var studentInfo = DynamicTuple.of("Alice", 28)` is added and compiled once, the following class is generated in the background.
```java
public record Tuple2<T0, T1> (
   T0 item0,
   T1 item1
) {}
```
3. An overload of the static method `DynamicTuple.of` is added.
```java
public class DynamicTuple {
    public static Object of(Object... args) {}
    public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {}
}
```
4. In the statement `var studentInfo = DynamicTuple.of("Alice", 28`, the type of `studentInfo` will be `Tuple2<String, Integer>` in the compile time.
5. Hence, if `n` typed arguments are given to `DynamicTuple.of` method, this library will
   1. Create a tuple `Tuple(n)<T0, T1, ... T(n)>`
   2. Create a corresponding overloaded factory method `Tuple(n)<T0, T1, ... T(n)> DynamicTuple.Of(T0 item0, T1 item1, ... T(n) item(n))`
6. These tuple classes and the static factory methods are additive. If one calls `DynamicTuple.Of` "n" times with "m" different arguments,
   1. "m" different tuple classes are created.
   2. "m" different static factory methods are created.
7. **Important Note**
   Refrain from creating objects with `new Tuple3("Alice", 28, "Wonderland")`, as this won't trigger tuple class creation. This piece of code will break if there are no occurrences of `DynamicTuple.of(T0 item0, T1 item1, T2 item2)`.

#### Example
Let's say I want a tuple to store a Student's name (String), age (Integer) and if they belong to hostel (Boolean).
1. Call `DynamicTuple.of` with necessary arguments.
```java
var studentInfo = DynamicTuple.of("Alice", 12, false);
```
2. The type of the `studentInfo` reference would be `Tuple3<String, Integer, Boolean>`, as this class will be generated.
```java
public record Tuple3<T0, T1, T2> (
   T0 item0,
   T1 item1,
   T2 item2
) {}
public class DynamicTuple {
    public static Object of(Object... args) {}
    public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {}
}
```
3. Their properties can be accessed via
```java
var studentInfo = DynamicTuple.of("Alice", 12, false);
String name = studentInfo.item0();
Integer age = studentInfo.item1();
Boolean isHosteler = studentInfo.item1();
```
### Named Tuples
1. A static method `DynamicTuple.named` is given by the library initially.
```java
public class DynamicTuple {
    public static T named(T type, FieldSpec<?>... fieldSpecs) {}
}
```
2. Give a unique class name considering the current package as the first parameter.
3. The rest of the parameters are the fields of the tuple, defined by the lambdas of FieldSpec<T> interface. The name of the field is dictated by the lambda's variable name.
```java
@FunctionalInterface
public interface FieldSpec<T> {
    T value(Object fieldName);
}
```
4. The `T type` param is just used to for overloading factory methods. A static field `type` is created for each named tuple class with null value. But the type is same as the tuple class. This is used to uniquely select an overloaded method. 
```java
public static final Student type = null;
```
#### Example
The below code generates a student record with two fields name and age within the same package.
```java
Student student = DynamicTuple.named(Student.type, name -> "Alice", age -> 12);
```
The fields are immutable and can be accessed via,
```java
String name = student.name();
int age = student.age();
```
#### Important Note
1. For the Student class to be auto generated, the code has to be compiled again.
2. These lambdas are just for specifying the field name. Both **field order and type** has to be preserved if this same Student class is referenced as the first param of the named tuple, within the same package. Else compilation would fail.

## Stream support - Zip Streams
1. A static method `Stream<Object> DynamicTuple.zip(Stream<Object>... streams)` is given by the library initially.
```java
public class DynamicTuple {
    public static Stream<Object> zip(Stream<Object>... streams) {}
}
```
2. The tuple class generation is also triggered by calling `DynamicTuple.zip(Stream<?>... streams)`.
3. Similar to `DynamicTuple.of`, calling `DynamicTuple.zip` will also, 
   1. Create tuple classes based on the number of arguments.
   2. Create a new static zip overloaded method with the given arguments.
### Example
This is example of calling `DynamicTuple.zip` with `Stream<String>`, `Stream<Integer>`, `Stream<Boolean>`
1. This zips the three individual streams into a single stream. The length of the stream will be the length of the smallest stream.
```java
Stream<String> nameStream = Stream.of("Alice", "Bob", "Carla");
Stream<Integer> ageStream = Stream.of(12, 13, 14);
Stream<Boolean> isHostelerStream = Stream.of(false, true, false);

var studentInfoStream = DynamicTuple.zip(nameStream, ageStream, isHostelerStream);
```
2. The return type of the above `DynamicTuple.zip` would be `Stream<Tuple3<String, Integer, Boolean>>`.
3. This will create the same `Tuple3<T0, T1, T2>` class `<T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2)` static factory, and also this static zip method. 
```java
public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {}
```
4. The `zip` method does not collect values internally. It's a intermediate operation. 