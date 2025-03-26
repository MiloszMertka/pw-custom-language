package pl.edu.pw.ee.llvm;

class LLVMGenerator {

    private static final StringBuilder HEADER_TEXT = new StringBuilder();
    private static final StringBuilder MAIN_TEXT = new StringBuilder();
    static int register = 1;
    static int str = 1;

    static void printf_i32(String id) {
        load_i32(id);
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32 %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void printf_i64(String id) {
        load_i64(id);
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i64 %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void printf_float(String id) {
        load_float(id);
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), float %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void printf_double(String id) {
        load_double(id);
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), double %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void declare_i32(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca i32\n");
    }

    static void declare_i64(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca i64\n");
    }

    static void declare_float(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca float\n");
    }

    static void declare_double(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca double\n");
    }

    static void declare_string(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca i8*\n");
    }

    static void assign_i32(String id, String value) {
        MAIN_TEXT.append("store i32 ")
                .append(value)
                .append(", i32* %")
                .append(id)
                .append("\n");
    }

    static void assign_i64(String id, String value) {
        MAIN_TEXT.append("store i64 ")
                .append(value)
                .append(", i64* %")
                .append(id)
                .append("\n");
    }

    static void assign_float(String id, String value) {
        MAIN_TEXT.append("store float ")
                .append(value)
                .append(", float* %")
                .append(id)
                .append("\n");
    }

    static void assign_double(String id, String value) {
        MAIN_TEXT.append("store double ")
                .append(value)
                .append(", double* %")
                .append(id)
                .append("\n");
    }

    static void assign_string(String id, String value) {
        if (value.startsWith("%")) {
            MAIN_TEXT.append("store i8* ")
                    .append(value)
                    .append(", i8** %")
                    .append(id)
                    .append("\n");
            return;
        }

        MAIN_TEXT.append("store i8* %")
                .append(value)
                .append(", i8** %")
                .append(id)
                .append("\n");
    }

    static void load_i32(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load i32, i32* %")
                .append(id)
                .append("\n");
        register++;
    }

    static void load_i64(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load i64, i64* %")
                .append(id)
                .append("\n");
        register++;
    }

    static void load_float(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load float, float* %")
                .append(id)
                .append("\n");
        register++;
    }

    static void load_double(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load double, double* %")
                .append(id)
                .append("\n");
        register++;
    }

    static void load_string(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load i8*, i8** %")
                .append(id)
                .append("\n");
        register++;
    }

    static void load_bool(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = load i1, i1* %")
                .append(id)
                .append("\n");
        register++;
    }

    static void declare_bool(String id) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca i1\n");
    }

    static void assign_bool(String id, String value) {
        MAIN_TEXT.append("store i1 ")
                .append(value)
                .append(", i1* %")
                .append(id)
                .append("\n");
    }

    static void printf_bool(String id) {
        load_bool(id);

        // Porównanie, czy wartość boola to 1 (true) czy 0 (false)
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = icmp eq i1 %")
                .append(register - 1)
                .append(", 1\n");
        register++;

        // Konwersja wyniku porównania na string ("true" lub "false")
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = select i1 %")
                .append(register - 1)
                .append(", i8* @truetext, i8* @falsetext\n");
        register++;

        // Wywołanie printf z formatem %s
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @strpb, i32 0, i32 0), i8* %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void add_i32(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = add i32 ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void add_i64(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = add i64 ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void sub_i32(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sub i32 ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void sub_i64(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sub i64 ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void add_float(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fadd float ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void add_double(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fadd double ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void sub_float(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fsub float ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void sub_double(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fsub double ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void mult_i32(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = mul i32 ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void mult_i64(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = mul i64 ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void div_i32(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sdiv i32 ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void div_i64(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sdiv i64 ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void mult_float(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fmul float ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void mult_double(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fmul double ")
                .append(val1)
                .append(", ")
                .append(val2)
                .append("\n");
        register++;
    }

    static void div_float(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fdiv float ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void div_double(String val1, String val2) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fdiv double ")
                .append(val2)
                .append(", ")
                .append(val1)
                .append("\n");
        register++;
    }

    static void sext_i32(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sext i32 ")
                .append(id)
                .append(" to i64\n");
        register++;
    }

    static void trunc_i64(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = trunc i64 ")
                .append(id)
                .append(" to i32\n");
        register++;
    }

    static void fpext_float(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fpext float ")
                .append(id)
                .append(" to double\n");
        register++;
    }

    static void fptrunc_double(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fptrunc double ")
                .append(id)
                .append(" to float\n");
        register++;
    }

    static void sitofp_int32_float(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sitofp i32 ")
                .append(id)
                .append(" to float\n");
        register++;
    }

    static void sitofp_i32_double(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sitofp i32 ")
                .append(id)
                .append(" to double\n");
        register++;
    }

    static void sitofp_i64_float(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sitofp i64 ")
                .append(id)
                .append(" to float\n");
        register++;
    }

    static void sitofp_i64_double(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = sitofp i64 ")
                .append(id)
                .append(" to double\n");
        register++;
    }

    static void fptosi_float_i32(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fptosi float ")
                .append(id)
                .append(" to i32\n");
        register++;
    }

    static void fptosi_float_i64(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fptosi float ")
                .append(id)
                .append(" to i64\n");
        register++;
    }

    static void fptosi_double_i32(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fptosi double ")
                .append(id)
                .append(" to i32\n");
        register++;
    }

    static void fptosi_double_i64(String id) {
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = fptosi double ")
                .append(id)
                .append(" to i64\n");
        register++;
    }

    static void allocate_string(String id, int length) {
        MAIN_TEXT.append("%")
                .append(id)
                .append(" = alloca [")
                .append(length + 1)
                .append(" x i8]\n");
    }

    static void printf_string(String id) {
        load_string(id);
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strps, i32 0, i32 0), i8* %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static void constant_string(String content) {
        final var length = content.length() + 1;
        HEADER_TEXT.append("@str")
                .append(str)
                .append(" = constant [")
                .append(length)
                .append(" x i8] c\"")
                .append(content)
                .append("\\00\"\n");
        final var id = "str" + str;
        LLVMGenerator.allocate_string(id, (length - 1));
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = bitcast [")
                .append(length)
                .append(" x i8]* %")
                .append(id)
                .append(" to i8*\n");
        MAIN_TEXT.append("call void @llvm.memcpy.p0i8.p0i8.i64(i8* align 1 %")
                .append(register)
                .append(", i8* align 1 getelementptr inbounds ([")
                .append(length)
                .append(" x i8], [")
                .append(length)
                .append(" x i8]* @")
                .append(id)
                .append(", i32 0, i32 0), i64 ")
                .append(length)
                .append(", i1 false)\n");
        register++;
        str++;
    }

    static void scanf(String id, int length) {
        allocate_string("str" + str, length);
        MAIN_TEXT.append("%").append(id).append(" = alloca i8*\n");
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = getelementptr inbounds [")
                .append(length + 1)
                .append(" x i8], [")
                .append(length + 1)
                .append(" x i8]* %str")
                .append(str)
                .append(", i64 0, i64 0\n");
        register++;
        MAIN_TEXT.append("store i8* %")
                .append(register - 1)
                .append(", i8** %")
                .append(id)
                .append("\n");
        str++;
        MAIN_TEXT.append("%")
                .append(register)
                .append(" = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @strs, i32 0, i32 0), i8* %")
                .append(register - 1)
                .append(")\n");
        register++;
    }

    static String generate() {
        return "declare i32 @printf(i8*, ...)\n" +
                "declare i32 @sprintf(i8*, i8*, ...)\n" +
                "declare i8* @strcpy(i8*, i8*)\n" +
                "declare i8* @strcat(i8*, i8*)\n" +
                "declare i32 @scanf(i8*, ...)\n" +
                "declare void @llvm.memcpy.p0i8.p0i8.i64(i8* noalias nocapture writeonly, i8* noalias nocapture readonly, i64, i1 immarg)\n" +
                "@strps = constant [4 x i8] c\"%s\\0A\\00\"\n" +
                "@strpi = constant [4 x i8] c\"%d\\0A\\00\"\n" +
                "@strpd = constant [4 x i8] c\"%f\\0A\\00\"\n" +
                "@strs = constant [5 x i8] c\"%10s\\00\"\n" +
                "@strspi = constant [3 x i8] c\"%d\\00\"\n" +
                "@strpb = constant [4 x i8] c\"%s\\0A\\00\"\n" +
                "@truetext = constant [5 x i8] c\"true\\00\"\n" +
                "@falsetext = constant [6 x i8] c\"false\\00\"\n" +
                HEADER_TEXT +
                "define i32 @main() nounwind{\n" +
                MAIN_TEXT +
                "ret i32 0 }\n";
    }

}
