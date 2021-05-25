package fluent.validation.result;

import fluent.validation.Check;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public final class XmlStreamResultVisitor implements ResultVisitor {

    private final XMLStreamWriter builder;

    public XmlStreamResultVisitor(XMLStreamWriter builder) {
        this.builder = builder;
    }

    @Override
    public void actual(Object actualValue, Result result) {
        result.accept(this);
    }

    @Override
    public void expectation(Object expectation, boolean result) {
        try {
            builder.writeStartElement("expectation");
            builder.writeAttribute("result", result ? "pass" : "fail");
            builder.writeCharacters(String.valueOf(expectation));
            builder.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transformation(Object name, Result dependency, boolean result) {
        try {
            builder.writeStartElement("expectation");
            builder.writeAttribute("name", String.valueOf(name));
            builder.writeAttribute("result", result ? "pass" : "fail");
            dependency.accept(this);
            builder.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aggregation(Object description, String glue, List<Result> itemResults, boolean result) {
        try {
            builder.writeStartElement("aggregation");
            builder.writeAttribute("name", String.valueOf(glue));
            builder.writeAttribute("result", result ? "pass" : "fail");
            for (Result itemResult : itemResults)
                itemResult.accept(this);
            builder.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tableAggregation(Object prefix, List<Check<?>> checks, List<?> items, List<TableInResult.Cell> results, boolean value) {
    }

    @Override
    public void error(Throwable throwable) {

    }

    @Override
    public void invert(Result result) {
        result.accept(this);
    }

}
