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
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;

import java.io.StringWriter;
import java.time.LocalDateTime;

import static fluent.validation.Checks.*;
import static fluent.validation.Items.items;
import static java.util.Collections.singletonList;
import static javax.xml.stream.XMLOutputFactory.newFactory;

public class XmlStreamResultVisitorTest {

    @Test(enabled = false)
    public void test() throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ActualValueInResult.class, ExpectationInResult.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        marshaller.marshal(new ActualValueInResult("AA", new ExpectationInResult("BB", false)), stringWriter);
        Assert.assertEquals(stringWriter.toString(), "CCC");
    }

    @Test(enabled = false)
    public void testXmlWriter() throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        XmlStreamResultVisitor visitor = new XmlStreamResultVisitor(newFactory().createXMLStreamWriter(stringWriter));
        Check.that("AAA", contains("A"), visitor);
        Assert.assertEquals(stringWriter.toString(), "FF");
    }

    @Test(enabled = false)
    public void testTable() throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        XmlStreamResultVisitor visitor = new XmlStreamResultVisitor(newFactory().createXMLStreamWriter(stringWriter));
        Check.that(singletonList(LocalDateTime.now()), collection(containsInAnyOrder(items())), visitor);
        Assert.assertEquals(stringWriter.toString(), "FF");

    }
}
