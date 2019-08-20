package fluent.validation.generator;

import fluent.api.generator.Templates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Templates("/fluent/validation/check.java")
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface FluentCheck {

    String packageName() default "";

    String className() default "";

    String factoryMethod() default "";

    boolean generic() default false;

}
