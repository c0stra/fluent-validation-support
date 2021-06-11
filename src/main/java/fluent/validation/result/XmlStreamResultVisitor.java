/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

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

    @Override
    public void soft(Result result) {
        try {
            builder.writeStartElement("soft");
            result.accept(this);
            builder.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
