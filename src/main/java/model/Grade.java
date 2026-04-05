package model;

public enum Grade {
    EXCELLENT(6),
    GOOD(5),
    AVERAGE(4),
    BELOW_AVERAGE(3),
    FAILING(2);

    private final int value;

    Grade(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
