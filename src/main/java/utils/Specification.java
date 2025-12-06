package utils;

import java.util.function.Predicate;

public interface Specification<T> {
    boolean isSatisfiedBy(T item);

    default Specification<T> and(Specification<T> other) {
        return item -> this.isSatisfiedBy(item) && other.isSatisfiedBy(item);
    }

    default Specification<T> or(Specification<T> other) {
        return item -> this.isSatisfiedBy(item) || other.isSatisfiedBy(item);
    }

    default Specification<T> not() {
        return item -> !this.isSatisfiedBy(item);
    }

    static <T> Specification<T> fromPredicate(Predicate<T> predicate) {
        return predicate::test;
    }
}
