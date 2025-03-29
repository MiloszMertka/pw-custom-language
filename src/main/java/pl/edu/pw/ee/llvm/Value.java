package pl.edu.pw.ee.llvm;

public class Value {

    private static final String GLOBAL_PREFIX = "@";
    private static final String LOCAL_PREFIX = "%";
    protected final String name;
    public PrimitiveType type;
    public int length;
    public boolean isGlobal;

    public Value(String name, PrimitiveType type, int length, boolean isGlobal) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.isGlobal = isGlobal;
    }

    public Value(String name, PrimitiveType type) {
        this.name = name;
        this.type = type;
        this.length = 0;
        this.isGlobal = false;
    }

    public Value withName(String name) {
        return new Value(name, type, length, isGlobal);
    }

    public String name() {
        final var prefix = isGlobal ? GLOBAL_PREFIX : LOCAL_PREFIX;
        return prefix + name;
    }

}
