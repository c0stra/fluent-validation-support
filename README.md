# Transparent validation framework
[![Released version](https://img.shields.io/maven-central/v/foundation.fluent.api/fluent-validation-support.svg)](https://search.maven.org/#search%7Cga%7C1%7Cfluent-validation-support)

The goal of this framework is to provide way of definition of any complex validation criteria, and get maximum detail,
when they are applied on tested data.

There are many ready to use factories and builders, but there is also support for simple definition of domain specific
builders (language).

Let's demonstrate the idea on following example validation. The following code throws assertion failure with
detailed drill-down of individual checks done within the whole evaluation.

```java
Person johnDoe = new Person("John Doe", 45, MALE);
Assert.that(johnDoe, BasicChecks.<Person>dsl()
        .withField("name", Person::getName).equalTo("John Doe")
        .withField("age", Person::getAge).matching(ComparisonChecks.moreThan(20))
        .withField("gender", Person::getGender).matching(oneOf(FEMALE)));
```

Error message drill down looks like:
```java
Assertion failed: expected: ((name <John Doe> and age > 20) and gender One of [FEMALE]) but was: test.Person@68be2bc2
	+ expected: gender One of [FEMALE] but was: <MALE>
```
See that first line displays whole expectation description (logical and of all items we've added), and actual value
(whole root object passed to the validation), and second line drills down to root cause of the failure, and explicitly
describes, that it was caused by evaluation of expectation of one individual field: "gender", and displays it's actual
value.

So the key features are:
1. Define complex validation criteria
2. Provide base for simple development of either custom criteria implementation or building domain specific builders
3. Get detailed information about evaluation of the expectation on actual data (transparent validation)
4. Implement custom presenters of that evaluation detail

This libraray comes with the concept of completely decoupled **expectation description** from **expectation application**.

- **Expectation description** in fact means, that we are first preparing whole complex expectation (and we do not have any
real data for testing available yet). Chapters 1.3 to 1.5 are only dealing with expectation description.

- **Expectation application** on the other hand means, that we apply the prepared expectation on our data, and get the
result, which can carry detailed information about the expectation evaluation. Chapters 1.1, 1.2, 2 and 3 are about
the evaluation and getting it's full detail.


## 1. Validation 

### 1.1 Evaluation
Checks can be used as simple (but transparent) predicate, returning `true` if,
supplied data satisfy the check, and `false` otherwise.

```java
boolean result = Check.that(data, isNull());
// Result of the check is returned, and code can continue
```

That allows to use the checks anywhere in decision logic of a test (or other program). Of course this
is almost equal to standard `Predicate`. But you may still benefit from the `Check` in cases, where
- Transparent evaluation providing detailed information is needed (see 2. Result representation)
- You have already predefined checks used elsewhere, so you can re-use them.


### 1.2 Assertion
More frequent usage of `Check` is in tests, where we need to make it fail whenever an
expectation represented by the `Check` is not met by the data under test.

In that case no return value is needed, but instead `AssertionFailure` is thrown.
Methods implementing such assertion are in the class `Assert`.

```java
Assert.that(data, isNull());
// AssertionFailure is thrown, which should terminate test execution.
```

### 1.3 Check factories
This library provides ready to use factories, allowing to build simple or complex checks.
The list of the methods here is not complete, as this is the main area of active development.
But it should provide comprehensive sample to get the idea. For complete list refer to static
methods of each of the factory classes.

#### 1.3.1 Simple basic checks ([`BasicChecks`](src/main/java/fluent/validation/BasicChecks.java))
Basic checks for objects in general are in the [`BasicChecks`](src/main/java/fluent/validation/BasicChecks.java) factory method. There are couple very common ones like:

- `equalTo(D)` - Check will compare supplied data to expected value. It has alias `is(D expectedValue)`.
- `sameInstance(D)` - checks, if reference is exactly the same object. It doesn't use `equals()` for comparison, but really `==`.
- `anything()` - always returns true
- `notNull()` - true for non null reference
- `isNull()` - true for null reference
- `instanceOf(Class)` - checks, if object is instance of provided class. Aliases: `isA(Class)`, `isAn(Class)`, `a(Class)`, `an(Class)`
- `sameClass(Class` - checks, if object's class is exactly the same as the expected class (it fails, if object is subclass of the expected).

For more see the [`BasicChecks`](src/main/java/fluent/validation/BasicChecks.java) class static public methods.

#### 1.3.2 Basic composition of checks
The whole idea of `Check` framework is to be able to apply complex validation. For that the improtant feature is composition.
There are different ways of composition. Here are the basic ones (available still in `BasicChecks`).

##### 1.3.2.1 Horizontal composition (boolean logic)
By horizontal composition I mean composition, where we compose multiple checks by some function, but where all
the checks are still applied on the same object.

Basic boolean binary operator composition is available directly as method of a `Check<D>`:
- `check.and(otherCheck)` - Resulting check returns true only if both `check` and `otherCheck` are true.
- `check.or(otherCheck)` - Resulting check returns true is any of `check` and `otherCheck` is true.

The chaining properly implements operator precedence. So `and` has higher priority than `or` even in following example:

```java
check1.or(check2).and(check3);
```
The resulting check will first do `check2 && check3` and only then `check1 || result`.

Chaining described above is upgrading. That means, that by chaining with more specific (but compatible) check, result is
also automatically more specialized. It is best described by following example:

```java
Check<Number> check1 = moreThan(number);
Check<Integer> check2 = lessThan(5);
// Chaining on check1 (Number) will specialize automatically to Integer: 
Check<Integer> check3 = check1.and(check2);
```

- `not(D)`, `not(Check<D>)` - Negation of a check
- `anyOf(Check<D>...)` - True if any of provided checks is satisfied by tested data
- `allOf(Check<D>...)` - True only if all provided checks are satisfied by tested data.
- `requireNonNull(Check<D>)` - Will fail if provided rererence is null, otherwise it will apply the provided check.

For more see the `basicChecks` class public methods.

##### 1.3.2.3 Vertical composition (transformation)
By vertical composition I mean composition, where original object is transformed, and provided "partial" check is applied on the result, instead of the original object.

- `compose(String name, Transformation<? super D, V> transformation, Check<? super V> check)` - is the basic method to compose transformed check. However it may fail to property check types, so more convenient way is builder
- `has(String name, Transformation<? super D, V> transformation).matching(Check<? super V>)` - Same composition. Keep in mind, that it requires original object not to be null, so it can safely run e.g. method references.
- `nullableHas(String name, Transformation<? super D, V> transformation).matching(Check<? super V)` - The same, but allows oritinal object to be null, assuming, that transformation will properly handle it.
- `has(String name, Transformation<? super D, V> transformation).equalTo(V)` - builder shortcut for simple test using expected value.

#### 1.3.3 String checks ([`StringChecks`](src/main/java/fluent/validation/StringChecks.java))

#### 1.3.4 Comparison checks ([`ComparisonChecks`](src/main/java/fluent/validation/ComparisonChecks.java))
Checks, that use comparison

- `moreThan(x)`, `lessThan(x)`, `equalOrMoreThan(x)`, `equalOrLessThan()` - Check with simple comparison, where `x` implements `Comparable`
- `moreThan(x, comparator)`, `lessThan(x, comparator)` etc. - Checks with externally provided comparator
- `between(a, b)`, `between(a, b, comparator)` - Range check excluding boundaries
- `betweenInclude(a, b)` etc. - Range check including boundaries

#### 1.3.5 Numeric checks ([`NumericChecks`](src/main/java/fluent/validation/NumericChecks.java))

#### 1.3.6 Collection checks ([`CollectionChecks`](src/main/java/fluent/validation/CollectionChecks.java))
Collection checks is a very important category of checks.

- Quantifiers
  - `exists(elementName, check)` - Validate that in provided `Iterable<T>` exists element, that matches provided `check`. It's
   fast pass, so it returns true on first match, and doesn't test the rest of the iterable.
  - `every(elementName, check)` - Validate that in provided `Iterable<T>` every element matches procided `check`. It's
   fast fail, so on first item, that doesn't match the check, it returns false, and do not excercise the remaining items.

- Exact match

- Exact match in any order

- Collection contains items matching provided checks

- Collection starts with exact sequence of provided checks

- Collection starts with items matching provided checks, but in any order

##### 1.3.6.1 Retrying

Try max 5 times to check result of query to external service with default delay between attempts
of 1 second:
```java
Assert.that(service, repeatMax(has("result", Service::get).equalTo("Response"), 5));
```

Try the same with custom delay 100ms
```java
Assert.that(service, repeatMax(has("result", Service::get).equalTo("Response"), 5, Duration.ofMillis(100)));
```

##### 1.3.6.2 Check asynchronous events
To validate asynchronous events we need to handle (store) them in separate thread, and verify in the
validation (test) thread. Principle is very similar to other collection checks, but this time
we have to limit it by timeout, and synchronize the thread. Let's use `BlockingQueue<T>` for it and
blocking checks:

Example of validating of exact match in any order of asynchronous events:
```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>();
new Thread(() -> {
    try {
        Thread.sleep(200);
        queue.add("A");
        Thread.sleep(200);
        queue.add("B");
        Thread.sleep(200);
        queue.add("C");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();
Assert.that(queue, queueEqualInAnyOrderTo(items("A", "C", "B"), Duration.ofSeconds(1)));
```
The main thread verification will successfully wait for all the items, and pass.

Keep in mind, that e.g. exact match of whole collection means, that no additional item was received within
the given timeout, so such verification finishes after additional timeout reached after receiving the last item.

#### 1.3.7 Database checks ([`DatabaseChecks`](src/main/java/fluent/validation/DatabaseChecks.java))

### 1.4 Fluent check builders

General fluent check builder allows to simply compose a check of individual fields / features of tested object.
That allows the example from the beginning (but now split it a bit more):

```java
Person johnDoe = new Person("John Doe", 45, MALE);
CheckDsl<Person> check = BasicCheck.dsl();
Assert.that(johnDoe, check
        .withField("name", Person::getName).equalTo("John Doe")
        .withField("age", Person::getAge).matching(moreThan(20))
        .withField("gender", Person::getGender).matching(oneOf(FEMALE)));
```
In fact it's implemented by `allOf()` partial checks, that may be created using vertical composition.

`CheckDsl.Final<T>` provides set these methods:
- `with(Check<? super T> check)` - simply adds check of the object.
- `withField(String name, Transformation<T, V>).matching(Check<? super V> check)` - adds check of a feature (field).

Although it's named builder, it's not really a builder. Every such chaining in fact creates new immutable check.
So following may result in 3 different checks:

```java
CheckDsl<Person> entry = BasicCheck.dsl();
CheckDsl<Person> nameCheck = entry.withField("name", Person::getName).equalTo("John Doe");
CheckDsl<Person> ageCheck = entry.withField("age", Person::getAge).matching(moreThan(20));
```
Each check is different object, `entry` doesn't check anything (always returns true), `nameCheck` does only check name,
and `ageCheck` does only check age.

### 1.5 Domain specific fluent check builder (DSL)

Fluent building of checks is very powerful, but the general form from previous chapter is not
very convenient for repetitive usage. Therefore it's useful to implement custom DSL.
For that simply extend the class `AbstractCheckDsl`:

```java
public class PersonCheck extends AbstractCheckDsl<Person> {

    // Need to provide factory to both constructors in order to create immutable checks.
    PersonCheck(Check<? super D> check) {
        super(check, PersonCheck::new);
    }

    PersonCheck() {
        super(PersonCheck::new);
    }

    // Convenience factory method
    public static PersonCheck personWith() {
        return new PersonCheck();
    }

    public PersonCheck name(Check<? super String> check) {
        return with("name", Person::getName).matching(check);
    }

    public PersonCheck name(String expectedValue) {
        return name(equalTo(expectedValue));
    }

    public PersonCheck age(Check<? super Integer> check) {
        return with("age", Person::getAge).matching(check);
    }

    public PersonCheck age(int expectedValue) {
        return name(equalTo(expectedValue));
    }

    // ...
}
```

Such custom DSL allows to use following:

```java
Assert.that(johnDoe, personWith().name"John Doe").age(moreThan(20));
```

## 2. Result representation

```java
Check<Object> check = BasicChecks.anything();
```

## 3. Custom check development
Of course no library is able to cover specific cases, that need to be tested in product specific
tests. Therefore this `Check` framework's main goal is not to limit anybody from extending it.

### 3.1 Simple implementation using `Predicate<T>`
The simplest way of implementing custom `Check` is simply by providing a lambda implementation
of the logic, and string description:

```java
Check<String> myCheck = BasicChecks.check(s -> s.split(",")[2].equals("A"), "comma separated list's item #2 is A")
```

The logic is used to evaluate if supplied data satisfies the check, and the string is used to describe
it in the transparent evaluation.

Simple usage like this:

```java
Assert.that("A,C,D", myCheck);
```
will result in following error:
```java
Expected: <comma separated list's item #2 is A> but was: <A,C,D>
```

Such check can be used in composition of any type mentioned above.

### 3.2 Extending `Check<T>` abstract class
The custom implementation of a specific check metnioned above is very simple, but in turn limited in the transparency
capabilities. In cases, that you also need/want to better handle evaluation detail description, you may need to
go and extend the abstract class `Check<D>` itself. By doing that you need to implement the abstract method `Result test(D data, ResultFactory resultFactory)`.
That gives you full control over the evaluation detail, that you want to provide.

#### 3.2.1 `ResultFactory` API

## 4. Custom validation result representation

### 4.1 `ResultVisitor` API
