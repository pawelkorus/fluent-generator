[![](https://jitpack.io/v/pawelkorus/fluent-generator.svg)](https://jitpack.io/#pawelkorus/fluent-generator)
# fluent-generator
fluent-generator allows defining and configuring generators for java beans and entities.

## Import
In order to import fluent-generator into your project you have to setup [JitPack repositories](https://jitpack.io).
Then if you use maven you can add following to your pom.xml file
```
<dependency>
    <groupId>com.github.pawelkorus</groupId>
    <artifactId>fluent-generator</artifactId>
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
## Built-in suppliers
## Using annotation processor
## Sample repository
For sample repository see [fluent-generator-sample](https://github.com/pawelkorus/fluent-generator-sample)
