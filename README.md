[![](https://jitpack.io/v/pawelkorus/fluent-generator.svg)](https://jitpack.io/#pawelkorus/fluent-generator)
# fluent-generator
Define your model:
```
@Data
class Person {
  String firstName;
  String lastName;
  int age;
}
```
Define generator interface:
```
interface PersonGenerator extends Generator<Person> {
  PersonGenerator firstName(Supplier<String> v);
  PersonGenerator lastName(Supplier<String> v);
  PersonGenerator age(Supplier<Integer>);
  Person build();
}
```
Create generator instance:
```
GeneratorFactory generatorFactory = new GeneratorFactory();
PersonGenerator generator = generatorFactory.generatorInstance(PersonGenerator.class);
```
Configure generator:
```
generator.firstName(() -> return "John").lastName(() -> return "Poultney").age(() -> return 5);
```
Build model:
```
Person johnPoultney = generator.build();
```
