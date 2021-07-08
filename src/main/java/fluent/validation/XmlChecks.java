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

package fluent.validation;

import fluent.validation.processor.Factory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import java.io.StringReader;

import static java.lang.Boolean.TRUE;

@Factory
public final class XmlChecks {

    private static final XPath xPath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private XmlChecks() {}

    private static XPathExpression compile(String xPath) {
        try {
            return XmlChecks.xPath.compile(xPath);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Check<Object> matchesXPath(String xPath) {
        XPathExpression xPathExpression = compile(xPath);
        return BasicChecks.check(xml -> TRUE.equals(xPathExpression.evaluate(xml, XPathConstants.BOOLEAN)), xPath);
    }

    public static TransformationBuilder<Object, Check<Object>> hasNode(String xPath, QName type) {
        XPathExpression xPathExpression = compile(xPath);
        return BasicChecks.has(xPath, xml -> xPathExpression.evaluate(xml, type));
    }

    public static TransformationBuilder<Object, Check<Object>> hasNode(String xPath) {
        return hasNode(xPath, XPathConstants.NODE);
    }

    public static TransformationBuilder<Object, Check<String>> hasTextContent(String xPath) {
        XPathExpression xPathExpression = compile(xPath);
        return BasicChecks.has(xPath, xPathExpression::evaluate);
    }

    /**
     * Use string parameter as XML content, and check the file using DOM Document check.
     *
     * @param check DOM Document check.
     * @return String content check.
     */
    public static Check<String> parseXml(Check<? super Document> check) {
        return BasicChecks.transform(string -> factory.newDocumentBuilder().parse(new InputSource(new StringReader(string))), check);
    }

    /**
     * Use string parameter as path to XML file, and check the file using DOM Document check.
     *
     * @param check DOM Document check.
     * @return File name check.
     */
    public static Check<String> loadXml(Check<? super Document> check) {
        return BasicChecks.transform(string -> factory.newDocumentBuilder().parse(string), check);
    }

}
