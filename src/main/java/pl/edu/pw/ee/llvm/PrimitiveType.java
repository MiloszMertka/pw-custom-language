package pl.edu.pw.ee.llvm;

public enum PrimitiveType {
    INT("i32", "strpi", "0"),
    LONG("i64", "strpi", "0"),
    FLOAT("float", "strpd", "0.0"),
    DOUBLE("double", "strpd", "0.0"),
    STRING("i8*", "strps", "null"),
    BOOLEAN("i1", "strpb", "0"),
    VOID("void", "", ""),
    UNKNOWN("", "", "");

    private final String llvmType;
    private final String llvmPrintPattern;
    private final String llvmZeroValue;

    PrimitiveType(String llvmType, String llvmPrintPattern, String llvmZeroValue) {
        this.llvmType = llvmType;
        this.llvmPrintPattern = llvmPrintPattern;
        this.llvmZeroValue = llvmZeroValue;
    }

    public String llvmType() {
        return llvmType;
    }

    public String llvmPrintPattern() {
        return llvmPrintPattern;
    }

    public String llvmZeroValue() {
        return llvmZeroValue;
    }
}
