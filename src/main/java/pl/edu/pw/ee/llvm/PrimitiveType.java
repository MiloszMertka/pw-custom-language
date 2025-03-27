package pl.edu.pw.ee.llvm;

public enum PrimitiveType {
    INT("i32"),
    LONG("i64"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("i8*"),
    BOOLEAN("i1"),
    UNKNOWN("");

    private final String llvmType;

    PrimitiveType(String llvmType) {
        this.llvmType = llvmType;
    }

    public String llvmType() {
        return llvmType;
    }
}
