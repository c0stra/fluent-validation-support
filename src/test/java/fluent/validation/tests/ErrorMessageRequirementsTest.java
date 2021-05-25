package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.Checks.*;
import static fluent.validation.CollectionChecks.*;
import static fluent.validation.StringChecks.startsWith;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

public class ErrorMessageRequirementsTest extends Requirements {{

    assertOf(
            "A"
    ).using(
            equalTo("B")
    ).shouldFailWith(
            "expected: <B> but was: <A>"
    );


    assertOf(
            1.0
    ).using(
            equalTo(2.0)
    ).shouldFailWith(
            "expected: <2.0 ±1.0E-6> but was: <1.0>"
    );


    assertOf(
            "A"
    ).using(
            not("A")
    ).shouldFailWith(
            "expected: not <A> but was: <A>"
    );


    assertOf(
            "A"
    ).using(
            not(allOf(equalTo("A"), startsWith("A")))
    ).shouldFailWith(
            "expected: not (<A> and starts with <A>) but was: A\n" +
            "\t+ expected: <A> but was: <A>\n" +
            "\t+ expected: starts with <A> but was: <A>"
    );


    assertOf(
            "A"
    ).using(
            allOf(not("A"), not("B"))
    ).shouldFailWith(
            "expected: (not <A> and not <B>) but was: A\n" +
            "\t+ expected: not <A> but was: <A>"
    );


    assertOf(
            null
    ).using(
            isNotNull()
    ).shouldFailWith(
            "expected: not <null> but was: <null>"
    );


    assertOf(
            null
    ).using(
            not(anything())
    ).shouldFailWith(
            "expected: not anything but was: <null>"
    );


    assertOf(
            "A"
    ).using(
            allOf(equalTo("A"), equalTo("B"))
    ).shouldFailWith(
            "expected: (<A> and <B>) but was: A\n" +
            "\t+ expected: <B> but was: <A>"
    );


    assertOf(
            "A"
    ).using(
            allOf(equalTo("C"), equalTo("B"))
    ).shouldFailWith(
            "expected: (<C> and <B>) but was: A\n" +
            "\t+ expected: <C> but was: <A>\n" +
            "\t+ expected: <B> but was: <A>"
    );


    assertOf(
            "A"
    ).using(
            has("toString", Object::toString).equalTo("B")
    ).shouldFailWith(
            "expected: toString <B> but was: <A>"
    );


    assertOf(
            "A"
    ).using(
            createBuilderWith(has(Object::toString).equalTo("B"))
                    .and(has(String::length).equalTo(4))
    ).shouldFailWith(
            "expected: (toString <B> and length <4>) but was: A\n" +
            "\t+ expected: toString <B> but was: <A>\n" +
            "\t+ expected: length <4> but was: <1>"
    );


    assertOf(
            singleton("A")
    ).using(
            exists("String", equalTo("B"))
    ).shouldFailWith(
            "expected: (<B>) but was: <B> not matched by any String\n" +
            "\t+ expected: <B> but was: <<B> not matched by any String>"
    );


    assertOf(
            asList("B", "A", "C")
    ).using(
            collection(equalInAnyOrderTo(items("B", "C", "F")))
    ).shouldFailWith(
            "expected: (<B>, <C>, <F>) in any order but: 1 check not satisfied\n" +
            "\t+ expected: <F> but was: <A>"
    );

}}
