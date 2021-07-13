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

package fluent.validation.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.type.TypeKind.*;

@SupportedAnnotationTypes("fluent.validation.processor.Factory")
public class FactoryGenerator extends AbstractProcessor {

    private static final Set<String> defaultPackages = new HashSet<>(asList("java.lang", "fluent.validation"));

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> factories = roundEnv.getElementsAnnotatedWith(Factory.class);
        if(factories.isEmpty()) {
            return false;
        }
        try(PrintWriter out = new PrintWriter(processingEnv.getFiler().createSourceFile("fluent.validation.Checks").openWriter())) {
            out.println("/*\n" +
                    " * BSD 2-Clause License\n" +
                    " *\n" +
                    " * Copyright (c) 2021, Ondrej Fischer\n" +
                    " * All rights reserved.\n" +
                    " *\n" +
                    " * Redistribution and use in source and binary forms, with or without\n" +
                    " * modification, are permitted provided that the following conditions are met:\n" +
                    " *\n" +
                    " *  Redistributions of source code must retain the above copyright notice, this\n" +
                    " *   list of conditions and the following disclaimer.\n" +
                    " *\n" +
                    " *  Redistributions in binary form must reproduce the above copyright notice,\n" +
                    " *   this list of conditions and the following disclaimer in the documentation\n" +
                    " *   and/or other materials provided with the distribution.\n" +
                    " *\n" +
                    " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
                    " * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
                    " * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n" +
                    " * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE\n" +
                    " * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL\n" +
                    " * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
                    " * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER\n" +
                    " * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,\n" +
                    " * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n" +
                    " * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" +
                    " *\n" +
                    " */\n");
            out.println("package fluent.validation;");
            out.println();
            out.println("/**\n" +
                    " * Factory of ready to use most frequent conditions. There are typical conditions for following categories:\n" +
                    " *\n" +
                    " * 1. General check builders for simple building of new conditions using predicate and description.\n" +
                    " * 2. General object conditions - e.g. isNull, notNull, equalTo, etc.\n" +
                    " * 3. Generalized logical operators (N-ary oneOf instead of binary or, N-ary allOf instead of binary and)\n" +
                    " * 4. Collection (Iterable) conditions + quantifiers\n" +
                    " * 5. Relational and range conditions for comparables\n" +
                    " * 6. String matching conditions (contains/startWith/endsWith as well as regexp matching)\n" +
                    " * 7. Basic XML conditions (XPath, attribute matching)\n" +
                    " * 8. Floating point comparison using a tolerance\n" +
                    " * 9. Builders for composition or collection of criteria.\n" +
                    " */\n");
            out.println("public final class Checks {");
            out.println();
            out.println("\tprivate Checks() {}");
            out.println();
            factories.stream().flatMap(factory -> ElementFilter.methodsIn(factory.getEnclosedElements()).stream()).filter(method -> method.getModifiers().contains(Modifier.PUBLIC)).forEach(method -> {
                out.println("\t/**");
                String comment = processingEnv.getElementUtils().getDocComment(method);
                if(nonNull(comment)) {
                    Stream.of(comment.split("\\R")).forEach(line -> out.println("\t *" + line));
                }
                out.println("\t * @see " + type(method.getEnclosingElement(), false) + "#" + sig(method, (p, i) -> type(p, false)));
                out.println("\t */");
                method.getAnnotationMirrors().forEach(a -> out.println("\t@" + type(a.getAnnotationType())));
                out.println("\tpublic static " + gen(method) + type(method.getReturnType()) + " " + sig(method, (p, i) -> type(p, i) + " " + p) + " {");
                out.println("\t\t" + "return " + type(method.getEnclosingElement(), false) + "." + sig(method, (v, i) -> v.toString()) + ";");
                out.println("\t}");
                out.println();
            });
            out.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String type(TypeMirror typeMirror) {
        return typeMirror.getKind() != DECLARED ? typeMirror.toString() : stripPkg(pkg(processingEnv.getTypeUtils().asElement(typeMirror)), typeMirror.toString());
    }

    private String type(Element element, boolean isVararg) {
        return isVararg ? type(((ArrayType)element.asType()).getComponentType()) + "..." : type(element.asType());
    }

    private String stripPkg(String pkg, String type) {
        return defaultPackages.contains(pkg) ? type.substring(pkg.length() + 1) : type;
    }

    private String pkg(Element element) {
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        return isNull(packageOf) ? "" : packageOf.toString();
    }

    private static String gen(ExecutableElement method) {
        return method.getTypeParameters().isEmpty() ? "" : method.getTypeParameters().stream().map(p -> p.getSimpleName() + " extends " + p.getBounds().stream().map(Objects::toString).collect(joining())).collect(joining(", ", "<", "> "));
    }

    private static String sig(ExecutableElement method, BiFunction<VariableElement, Boolean, String> arg) {
        List<? extends VariableElement> parameters = method.getParameters();
        int size = parameters.size();
        return method.getSimpleName() + "(" + IntStream.range(0, size)
                .mapToObj(i -> arg.apply(parameters.get(i), method.isVarArgs() && i == size - 1))
                .collect(joining(", ")) + ")";
    }
}
