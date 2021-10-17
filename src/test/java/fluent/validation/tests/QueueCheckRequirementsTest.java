package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.CollectionChecks.*;
import static fluent.validation.Items.items;
import static fluent.validation.utils.Mocks.asQueue;

public class QueueCheckRequirementsTest extends Requirements {{

        testOf (asQueue("A", "C", "D")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "E", "D")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "A", "C", "D")). using (queue(equalTo(items("A", "C", "D")))). shouldReturn (false);

        testOf (asQueue("A", "C", "D")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "E", "D")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "A", "C", "D")). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue()). using (queue(equalInAnyOrderTo(items()))). shouldReturn (true);
        testOf (asQueue()). using (queue(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);

        testOf (asQueue("A", "C", "D")). using (queue(contains(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(contains(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C")). using (queue(contains(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(contains(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C", "E", "D")). using (queue(contains(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("E", "A", "C", "D")). using (queue(contains(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C", "E")). using (queue(contains(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("C", "A", "E", "D")). using (queue(contains(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "D", "C", "A")). using (queue(contains(items("A", "C", "D")))). shouldReturn (false);

        testOf (asQueue("A", "C", "D")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C", "E", "D")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("E", "A", "C", "D")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C", "E")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("C", "A", "E", "D")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("E", "D", "C", "A")). using (queue(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);


        testOf (asQueue("A", "C", "D")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C", "E", "D")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "A", "C", "D")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "D", "C", "E")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("C", "A", "E", "D")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "D", "C", "A")). using (queue(startsWith(items("A", "C", "D")))). shouldReturn (false);

        testOf (asQueue("A", "C", "D")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "D", "C")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "C", "D", "E")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("A", "C", "E", "D")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "A", "C", "D")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("A", "D", "C", "E")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
        testOf (asQueue("C", "A", "E", "D")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
        testOf (asQueue("E", "D", "C", "A")). using (queue(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);

}}
