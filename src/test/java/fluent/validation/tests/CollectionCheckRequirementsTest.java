package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.CollectionChecks.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class CollectionCheckRequirementsTest extends Requirements {{

        testOf(asList("A", "C", "D")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "E", "D")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "A", "C", "D")).using(collection(equalTo(items("A", "C", "D")))).shouldReturn(false);

        testOf(asList("A", "C", "D")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "E", "D")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "A", "C", "D")).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(false);
        testOf(emptyList()).using(collection(equalInAnyOrderTo(items()))).shouldReturn(true);
        testOf(emptyList()).using(collection(equalInAnyOrderTo(items("A", "C", "D")))).shouldReturn(false);

        testOf(asList("A", "C", "D")).using(collection(contains(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(contains(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C")).using(collection(contains(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(contains(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C", "E", "D")).using(collection(contains(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("E", "A", "C", "D")).using(collection(contains(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C", "E")).using(collection(contains(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("C", "A", "E", "D")).using(collection(contains(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "D", "C", "A")).using(collection(contains(items("A", "C", "D")))).shouldReturn(false);

        testOf(asList("A", "C", "D")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C", "E", "D")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("E", "A", "C", "D")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C", "E")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("C", "A", "E", "D")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("E", "D", "C", "A")).using(collection(containsInAnyOrder(items("A", "C", "D")))).shouldReturn(true);


        testOf(asList("A", "C", "D")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C", "E", "D")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "A", "C", "D")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "D", "C", "E")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("C", "A", "E", "D")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "D", "C", "A")).using(collection(startsWith(items("A", "C", "D")))).shouldReturn(false);

        testOf(asList("A", "C", "D")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "D", "C")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "C", "D", "E")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("A", "C", "E", "D")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "A", "C", "D")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("A", "D", "C", "E")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(true);
        testOf(asList("C", "A", "E", "D")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(false);
        testOf(asList("E", "D", "C", "A")).using(collection(startsInAnyOrderWith(items("A", "C", "D")))).shouldReturn(false);

}}
