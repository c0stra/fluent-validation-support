package fluent.validation;

import fluent.validation.detail.CheckVisitor;
import fluent.validation.utils.Mocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import static fluent.validation.Checks.anything;
import static fluent.validation.Checks.dsl;
import static fluent.validation.Checks.transparent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomCheckVisitorTest extends Mocks {

    @Mock
    private CheckVisitor mockVisitor;

    @Test
    public void testMismatch() {
        when(mockVisitor.node(any())).thenReturn(mockVisitor);
        when(mockVisitor.label(any())).thenReturn(mockVisitor);
        when(mockVisitor.negative(any())).thenReturn(mockVisitor);
        try {
            Check.that("A", dsl().with(transparent(mockVisitor, anything())).withField("toString", Object::toString).equalTo("A"));
        } catch (AssertionFailure failure) {
            verify(mockVisitor).node(any());
        }
    }

}
