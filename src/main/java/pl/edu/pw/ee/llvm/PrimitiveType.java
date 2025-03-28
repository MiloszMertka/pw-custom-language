package pl.edu.pw.ee.llvm;

public enum PrimitiveType {
    INT("i32", "strpi"),
    LONG("i64", "strpi"),
    FLOAT("float", "strpd"),
    DOUBLE("double", "strpd"),
    STRING("i8*", "strps"),
    BOOLEAN("i1", "strpb"),
    VOID("void", ""),
    UNKNOWN("", "");

    private final String llvmType;
    private final String llvmPrintPattern;

    PrimitiveType(String llvmType, String llvmPrintPattern) {
        this.llvmType = llvmType;
        this.llvmPrintPattern = llvmPrintPattern;
    }

    public String llvmType() {
        return llvmType;
    }

    public String llvmPrintPattern() {
        return llvmPrintPattern;
    }
}
