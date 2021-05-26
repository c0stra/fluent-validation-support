package fluent.validation.processor;

/**
 * To avoid conflicts in case of factory methods with the same name in multiple factory classes, when merged to "uber"
 * factory class Checks, use this annotation to define alternative name of the method, when added to Checks.
 */
public @interface ChecksName {

    /**
     * Name of the method, when added to Checks factory class.
     * @return Alternative name.
     */
    String value();

}
