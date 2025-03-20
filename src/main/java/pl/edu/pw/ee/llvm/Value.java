package pl.edu.pw.ee.llvm;

public class Value {

    public String name;
    public PrimitiveType type;

    public Value(String name, PrimitiveType type) {
        this.name = name;
        this.type = type;
    }

}
