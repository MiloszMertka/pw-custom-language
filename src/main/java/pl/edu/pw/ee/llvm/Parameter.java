package pl.edu.pw.ee.llvm;

public class Parameter extends Value {

    public Parameter(String name, PrimitiveType type) {
        super(name, type);
        isGlobal = false;
    }

}
