package fluent.validation;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import java.io.StringReader;

import static java.lang.Boolean.TRUE;

public class XmlChecks {

    private static final XPath xPath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

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

    public static CheckBuilder<Object, Check<Object>> hasNode(String xPath, QName type) {
        XPathExpression xPathExpression = compile(xPath);
        return BasicChecks.has(xPath, xml -> xPathExpression.evaluate(xml, type));
    }

    public static CheckBuilder<Object, Check<Object>> hasNode(String xPath) {
        return hasNode(xPath, XPathConstants.NODE);
    }

    public static CheckBuilder<Object, Check<String>> hasXPath(String xPath) {
        XPathExpression xPathExpression = compile(xPath);
        return BasicChecks.has(xPath, xPathExpression::evaluate);
    }

    /**
     * Use string parameter as XML content, and check the file using DOM Document check.
     *
     * @param check DOM Document check.
     * @return String content check.
     */
    public static Check<String> asXml(Check<? super Document> check) {
        return BasicChecks.transform(string -> factory.newDocumentBuilder().parse(new InputSource(new StringReader(string))), check);
    }

    /**
     * Use string parameter as path to XML file, and check the file using DOM Document check.
     *
     * @param check DOM Document check.
     * @return File name check.
     */
    public static Check<String> asXmlFile(Check<? super Document> check) {
        return BasicChecks.transform(string -> factory.newDocumentBuilder().parse(string), check);
    }

}
