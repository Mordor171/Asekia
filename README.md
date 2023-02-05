# Asekia Usage Manual

## What is Asekia

Asekia is a lightweight Java desktop app framework that includes some of Spring's features like: IoC, DI, AOP, BeanFactory, etc. Unfortunately, Asekia is not compatible with Spring and most components in the Spring eco, I will implement these features as soon as possible. After all, Asekia is still in the development stage.

## How to use Asekia

### Import

You can introduce Asekia as a dependency into your project via Maven or Gradle (here version 1.0-RELEASE is used as an example).

```properties
<dependency>
    <groupId>run.asekia.app</groupId>
    <artifactId>Asekia</artifactId>
    <version>1.0-RELEASE</version>
</dependency>
```

```gradle
implementation group: 'run.asekia.app', name: 'Asekia', version: 1.0-RELEASE'
```

### Usage

#### ShredFactory

Like most Spring applications, Asekia has its own `BeanFactory` called `ShredFactory`, which implements Shred's instantiation and injection work. `@Shred` annotations allow you to autowired a class into a factory during Asekia startup, which in turn can be used as `@Inject` a global singleton object in other classes. Here is a simple example:

```java
// Teacher.java
@Shred
public class Teacher() {
    @Inject
    Student student;
    private String name = "Danfoss";
}

// Student.java
@Shred
public class Student() {
    @Inject
    Student Teacher;
    private String name = "Casilla";
}
```

#### Configuration File


