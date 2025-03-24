package pl.edu.pw.ee.llvm;

public class Value {

    public String name;
    public PrimitiveType type;
    public int length;

    public Value(String name, PrimitiveType type, int length) {
        this.name = name;
        this.type = type;
        this.length = length;
    }

    public Value(String name, PrimitiveType type) {
        this.name = name;
        this.type = type;
        this.length = 0;
    }

}
