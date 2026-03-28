package model;

public enum Grade {
    OTLICEN(6),
    MNOGO_DOBAR(5),
    DOBAR(4),
    SREDEN(3),
    SLAB(2);

    private final int value;

    Grade(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
