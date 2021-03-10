package fluent.validation;

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.util.Map;

import static fluent.validation.BasicChecks.*;

public class MapCheckBuilder<K, V> extends Check<Map<K, V>> {

    private final Check<? super Map<K, V>> check;

    public MapCheckBuilder(Check<? super Map<K, V>> check) {
        this.check = check;
    }

    public MapCheckBuilder() {
        this(anything());
    }

    @Override
    protected Result evaluate(Map<K, V> data, ResultFactory factory) {
        return check.evaluate(data, factory);
    }

    @Override
    public String toString() {
        return check.toString();
    }

    public MapCheckBuilder<K, V> with(K key, Check<? super V> check) {
        return new MapCheckBuilder<>(this.check.and(new MapItemCheck<>(key, check)));
    }

    public MapCheckBuilder<K, V> with(K key, V expectedValue) {
        return with(key, equalTo(expectedValue));
    }
}
