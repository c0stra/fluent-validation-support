package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.Checks.*;

public class ComparisonRequirementsTest extends Requirements {{

    testOf(10).using(lessThan(11)).shouldReturn(true);
    testOf(10).using(lessThan(10)).shouldReturn(false);
    testOf(10).using(lessThan(9)).shouldReturn(false);

    testOf(10).using(equalOrLessThan(11)).shouldReturn(true);
    testOf(10).using(equalOrLessThan(10)).shouldReturn(true);
    testOf(10).using(equalOrLessThan(9)).shouldReturn(false);

    testOf(10).using(moreThan(11)).shouldReturn(false);
    testOf(10).using(moreThan(10)).shouldReturn(false);
    testOf(10).using(moreThan(9)).shouldReturn(true);

    testOf(10).using(equalOrMoreThan(11)).shouldReturn(false);
    testOf(10).using(equalOrMoreThan(10)).shouldReturn(true);
    testOf(10).using(equalOrMoreThan(9)).shouldReturn(true);

    testOf(10).using(between(8, 11)).shouldReturn(true);
    testOf(10).using(between(10, 12)).shouldReturn(false);
    testOf(10).using(between(8, 10)).shouldReturn(false);
    testOf(10).using(between(8, 9)).shouldReturn(false);
    testOf(10).using(between(11, 12)).shouldReturn(false);

    testOf(10).using(betweenInclusive(8, 11)).shouldReturn(true);
    testOf(10).using(betweenInclusive(10, 12)).shouldReturn(true);
    testOf(10).using(betweenInclusive(8, 10)).shouldReturn(true);
    testOf(10).using(betweenInclusive(8, 9)).shouldReturn(false);
    testOf(10).using(betweenInclusive(11, 12)).shouldReturn(false);

}}
