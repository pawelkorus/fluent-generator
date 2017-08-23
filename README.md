[![](https://jitpack.io/v/pawelkorus/fluent-generator.svg)](https://jitpack.io/#pawelkorus/fluent-generator)
# fluent-generator
fluent-generator allows defining and configuring generators for java beans and entities.

## Import
In order to import fluent-generator into your project you have to setup [JitPack repositories](https://jitpack.io).
Then if you use maven you can add following to your pom.xml file
```
<dependency>
    <groupId>com.github.pawelkorus</groupId>
    <artifactId>fluent-generator-lib</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
or if you use gradle
```
dependencies {
    compile 'com.github.pawelkorus:fluent-generator:master-SNAPSHOT'
}
```
## Usage
Let's assume that following bean class was defined:
```
@Data
class Shape {
  String id;
  String type;
  int size;
}
```
For classes like `Shape` fluent generator may be defined in following way:
```
interface ShapeGenerator extends Generator<Shape> {
  ShapeGenerator id(Supplier<String> v);
  ShapeGenerator type(Supplier<String> v);
  ShapeGenerator size(Supplier<Integer>);
  ShapeGenerator size(int age);
  Shape build();
}
```
The simplest way to create instance of `ShapeGenerator` is to use `GeneratorFactory` in the following way:
```
GeneratorFactory generatorFactory = new ReflectGeneratorProxyFactory();
ShapeGenerator generator = generatorFactory.generatorInstance(ShapeGenerator.class);
```
Next, `ShapeGenerator` instance may be configured so that it is able to create desired beans. For instance it may
be configured so that every `Shape` instance has id, type and size fields set to some constant values: 
```
generator.id(() -> return "shape1").type(() -> return "Rect").size(5);
```
Eventually, call to method `build` will produce new instance of `Shape` class with fields filled in: 
```
Shape rect1 = generator.build();
```
Next we may imagine that we need 100 instances of `Shape` class and all of those instance should have following
properties: id set to random UUID, type set to one of two values "Rect" and "Square" and size choosen randomly.
To meet this requirements `ShapeGenerator` instance needs to be configured as follows:
```
generator
    .id(() -> return UUID.randomUUID())
    .type(oneOf(Arrays.asList("Rect", "Square")))
    .size(randomInt(4, 10));
```
In this configuration `oneOf` and `randomInt` methods come from `fluent-generator-supplier` module. For more
details see [Built-in suppliers](#built-in-suppliers).

Because of the fact that `ShapeGenerator` class implementes indirectly `Supplier` interface it nicely works 
with Java 8 streams:
```
Stream.generate(generator).limit(100).collect(Collectors.toList())
```
## Built-in suppliers
## Using annotation processor
Annotation processor may be used in order to automatically generate source code for fluent generator
interfaces. Use `fluent-generator-processor` module to enable annotation processing.
## Sample repository
For sample repository see [fluent-generator-sample](https://github.com/pawelkorus/fluent-generator-sample)
