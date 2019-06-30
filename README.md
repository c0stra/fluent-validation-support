# Transparent validation framework
Library, providing ready to use procedural or fluent builders of various validation criteria as well as base for
development of completely custom fluent, domain specific validators.

This libraray comes with the concept of completely decoupled **expectation description** from **expectation application**.

**Expectation description** in fact means, that we are first preparing whole complex expectation (and we do not have any
real data for testing available yet). Chapters 1.3 to 1.5 are only dealing with expectation description.

**Expectation application** on the other hand means, that we apply the prepared expectation on our data, and get the
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

#### 1.3.1 Simple basic checks
Basic checks for objects in general are in the `BasicChecks` factory method. There are couple very common ones like:

- `equalTo(D)` - Check will compare supplied data to expected value. It has alias `is(D expectedValue)`.
- `sameInstance(D)` - checks, if reference is exactly the same object. It doesn't use `equals()` for comparison, but really `==`.
- `anything()` - always returns true
- `notNull()` - true for non null reference
- `isNull()` - true for null reference
- `instanceOf(Class)` - checks, if object is instance of provided class. Aliases: `isA(Class)`, `isAn(Class)`, `a(Class)`, `an(Class)`
- `sameClass(Class` - checks, if object's class is exactly the same as the expected class (it fails, if object is subclass of the expected).

For more see the `BasicChecks` class static public methods.

#### 1.3.2 Basic composition of checks
The whole idea of `Check` framework is to be able to apply complex validation. For that the improtant feature is composition.
There are different ways of composition. Here are the basic ones (available still in `BasicChecks`).

##### 1.3.2.1 Horizontal composition (boolean logic)
By horizontal composition I mean composition, where we compose multiple checks by some function, but where all
the checks are still applied on the same object.

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

#### 1.3.3 String checks (`StringChecks`)

#### 1.3.4 Comparison checks (`ComparisonChecks`)

#### 1.3.5 Numeric checks (`NumericChecks`)

#### 1.3.6 Collection checks (`CollectionChecks`)

##### 1.3.6.1 Retrying

##### 1.3.6.2 Check asynchronous events

#### 1.3.7 Database checks (`DatabaseChecks`)

### 1.4 Fluent check builders

### 1.5 Custom fluent check builder

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
