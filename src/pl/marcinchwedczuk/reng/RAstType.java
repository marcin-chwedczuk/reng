package pl.marcinchwedczuk.reng;

public enum RAstType {
    // Regex: ^
    AT_BEGINNING(100),

    // Regex: $
    AT_END(100),

    // Regex: [abc]
    // Single chars are converted to single chars groups,
    // like a -> [a].
    GROUP(100),

    // Regex: [^abc]
    INVERTED_GROUP(100),

    // Match single expression zero or more times.
    // Greedy by default.
    // Regex: R*
    REPEAT(80),

    // Match list of regexes one by one.
    // Regex: R_1 R_2 R_3
    CONCAT(70),

    // Match any of expressions
    // Regex: R_1 | R_2 | R_3
    ALTERNATIVE(20);

    public final int priority;

    RAstType(int priority) {
        this.priority = priority;
    }
}
