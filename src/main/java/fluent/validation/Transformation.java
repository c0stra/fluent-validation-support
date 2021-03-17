package fluent.validation;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * Functional interface representing transformation of a data for partial check.
 *
 * @param <F> Original type, on which the transformation is applied (FROM).
 * @param <T> Result type, that the transformation returns (TO).
 */
@FunctionalInterface
public interface Transformation<F, T> extends Serializable {

    /**
     * Apply the transformation.
     * @param from Original object, on which to apply the transformation.
     * @return Returns transformed value.
     * @throws Exception Can throw any exception during transformation.
     */
    T apply(F from) throws Exception;

    default String getMethodName() {
        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) writeReplace.invoke(this);
            writeReplace.setAccessible(false);
            String methodName = sl.getImplMethodName();
            return methodName.startsWith("get") ? methodName.substring(3) : methodName;
        } catch (Exception e) {
            return null;
        }
    }

}
