{% set productType = empty(var) ? type : var.type %}
{% set packageName = (packageName == "") ? (empty(var) ? type.packageName : var.packageName) : packageName %}
{% set classSuffix = concat(capitalize(methodName), "er").replaceFirst("teer", "tor").replaceFirst("eer", "er") %}
{% set className = (className == "") ? concat(productType.simpleName, classSuffix) : className %}
{% set classParameters = empty(productType.parameterVariables) ? "" : concat("<", join(productType.parameterVariables, ", "), ">") %}
package {{ packageName }};

import javax.annotation.Generated;
import fluent.validation.Check;
import static fluent.validation.Checks.equalTo;

@Generated("Generated code using {{ templatePath }}")
public final class {{ className }}{% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %}
    extends AbstractCheckDsl<{{ className }}{{ classParameters }}, productType> {

    public {{ className }}(Check<? super {{ productType }}> check) {
        super(check, {{ className }}::new);
    }

    public {{ className }}() {
        super({{ className }}::new);
    }

{% for getter in productType.methods %}{% if getter.name.startsWith("get") and getter.parameters.size == 0 %}
    public {{ className }}{{ classParameters }} {{ getter.propertyName }}(Check<? super {{ getter.returnType.wrapper }}> expectation) {
        return withField("{{getter.propertyName}}", {{ productType }}::{{ getter }}).matches(expectation);
    }

    public {{ className }}{{ classParameters }} {{ getter.propertyName }}({{ getter.returnType }} expectedValue) {
        return {{ getter.propertyName }}(equalTo(expectedValue));
    }

{% endif %}{% endfor %}

}
