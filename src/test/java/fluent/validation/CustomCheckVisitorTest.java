package fluent.validation;

import fluent.validation.detail.CheckVisitor;
import fluent.validation.utils.Mocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import static fluent.validation.Checks.anything;
import static fluent.validation.Checks.transparent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class CustomCheckVisitorTest extends Mocks {

    @Mock
    private CheckVisitor mockVisitor;

    @Test
    public void testMismatch() {
        try {
            new CheckDsl.Final<>(transparent(mockVisitor, anything())).withField("toString", Object::toString).equalTo("A").assertData("A");
        } catch (AssertionFailure failure) {
            verify(mockVisitor).node(any());
        }
    }

}
