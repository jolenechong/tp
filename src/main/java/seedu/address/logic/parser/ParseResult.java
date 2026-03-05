package seedu.address.logic.parser;

import java.util.Optional;

/**
 * Represents the result of parsing a string into an object of type T.
 * It contains the parsed value and an optional string for warnings.
 *
 * @param <T> the type of the parsed value.
 */
public class ParseResult<T> {
    private final T value;
    private final Optional<String> warning;

    /**
     * Constructs a ParseResult with the given value and an optional warning.
     *
     * @param value   the parsed value of type T.
     * @param warning an optional string containing any warnings related to the parsing process.
     */
    public ParseResult(T value, Optional<String> warning) {
        this.value = value;
        this.warning = warning;
    }

    public T getValue() {
        return value;
    }

    public Optional<String> getWarning() {
        return warning;
    }
}
