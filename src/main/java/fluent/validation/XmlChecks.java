package fluent.validation;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;

import static java.lang.Boolean.TRUE;

public class XmlChecks {

    private static final XPath xPath = XPathFactory.newInstance().newXPath();

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

}
