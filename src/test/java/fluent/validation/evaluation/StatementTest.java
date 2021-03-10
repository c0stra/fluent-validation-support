package fluent.validation.evaluation;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static fluent.validation.StringChecks.contains;
import static fluent.validation.XmlChecks.matchesXPath;

public class StatementTest extends Analyzer {

    public StatementTest() {
        super(new SimpleContext());
    }

    @Test
    public void test() {
        analysis("Baha", null);
    }

    private final Statement applicationIsDown = new Statement();
    private final Statement marketClosed = new Statement();
    private final Statement usHolidays = new Statement();

    public void analysis(String error, Document report) {

        when (error, contains("aha")). then (applicationIsDown);

        when (report, matchesXPath("/report/log/event")). then (marketClosed);

        when (applicationIsDown). and (marketClosed). then (usHolidays);

    }

}