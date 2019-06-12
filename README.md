# Transparent validation framework
Library, providing ready to use procedural or fluent builders of various validation criteria as well as base for
development of completely custom fluent, domain specific validators.

## 1. Validation 

### 1.1 Evaluation

```java
boolean result = Check.that(data, isNull());
```

### 1.2 Assertion

```java
Assert.that(data, isNull());
```
### 1.1 Check factories

### 1.2 Fluent check builders

### 1.3 Custom fluent check builder

## 2. Result representation

```java
Check<Object> check = StringChecks.anything();
```

## 3. Custom check development

### 3.1 Simple implementation using `Predicate<T>`

### 3.2 Extending `Check<T>` abstract class

#### 3.2.1 `ResultFactory` API

## 4. Custom validation result representation

### 4.1 `ResultVisitor` API
