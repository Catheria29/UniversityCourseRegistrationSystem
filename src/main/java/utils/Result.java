package utils;
import lombok.Getter;

import java.util.function.Function;

@Getter
public final class Result<T> {
    private final T value;
    private final String error;

    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> fail(String error) {
        return new Result<>(null, error);
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isOk() {
        return error == null;
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (hasError()) return fail(error);
        return ok(mapper.apply(value));
    }

    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (hasError()) return fail(error);
        return mapper.apply(value);
    }
}

