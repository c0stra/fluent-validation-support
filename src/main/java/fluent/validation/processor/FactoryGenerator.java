/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [LICENSOR] "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package fluent.validation.processor;

import fluent.validation.Check;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@SupportedAnnotationTypes("fluent.validation.processor.Factory")
public class FactoryGenerator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> factories = roundEnv.getElementsAnnotatedWith(Factory.class);
        if(factories.isEmpty()) {
            return false;
        }
        try(PrintWriter out = new PrintWriter(processingEnv.getFiler().createSourceFile(Check.class.getCanonicalName() + "s2").openWriter())) {
            out.println("package fluent.validation;");
            out.println();
            out.println("public final class Checks2 {");
            out.println();
            factories.stream().flatMap(factory -> ElementFilter.methodsIn(factory.getEnclosedElements()).stream()).filter(method -> method.getModifiers().contains(Modifier.PUBLIC)).forEach(method -> {
                out.println("\tpublic static " + gen(method) + method.getReturnType() + " " + sig(method, p -> p.asType() + " " + p) + " {");
                out.println("\t\t" + "return " + method.getEnclosingElement() + "." + sig(method, Objects::toString) + ";");
                out.println("\t}");
                out.println();
            });
            out.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String gen(ExecutableElement method) {
        return method.getTypeParameters().isEmpty() ? "" : method.getTypeParameters().stream().map(p -> p.getSimpleName() + " extends " + p.getBounds().stream().map(Objects::toString).collect(joining())).collect(joining(", ", "<", "> "));
    }

    private static String sig(ExecutableElement method, Function<VariableElement, String> arg) {
        return method.getSimpleName() + "(" + method.getParameters().stream().map(arg).collect(joining(", ")) + ")";
    }
}
