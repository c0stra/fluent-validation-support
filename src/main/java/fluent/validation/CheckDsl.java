package fluent.validation;

public class CheckDsl<D> extends AbstractCheckDsl<CheckDsl<D>, D> {

    CheckDsl(Check<? super D> check) {
        super(check, CheckDsl::new);
    }

    CheckDsl() {
        super(CheckDsl::new);
    }

}
