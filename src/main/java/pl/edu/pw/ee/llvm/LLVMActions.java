package pl.edu.pw.ee.llvm;

import pl.edu.pw.ee.HolyJavaBaseListener;
import pl.edu.pw.ee.HolyJavaParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class LLVMActions extends HolyJavaBaseListener {

    private static final Path OUTPUT_FILE_PATH = Path.of("output.ll");
    private final Map<String, PrimitiveType> variables = new HashMap<>();
    private final Stack<Value> stack = new Stack<>();

    @Override
    public void exitProgramme(HolyJavaParser.ProgrammeContext context) {
        try {
            final var llvmCode = LLVMGenerator.generate();
            Files.writeString(OUTPUT_FILE_PATH, llvmCode);
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    @Override
    public void exitAssign(HolyJavaParser.AssignContext context) {
        final var ID = context.ID().getText();
        final var variable = stack.pop();
        variables.put(ID, variable.type);

        if (variable.type == PrimitiveType.INT) {
            LLVMGenerator.declare_i32(ID);
            LLVMGenerator.assign_i32(ID, variable.name);
        }

        if (variable.type == PrimitiveType.REAL) {
            LLVMGenerator.declare_double(ID);
            LLVMGenerator.assign_double(ID, variable.name);
        }
    }

    @Override
    public void exitPrint(HolyJavaParser.PrintContext context) {
        final var ID = context.ID().getText();
        final var type = variables.get(ID);
        switch (type) {
            case INT -> LLVMGenerator.printf_i32(ID);
            case REAL -> LLVMGenerator.printf_double(ID);
            case UNKNOWN -> error(context.getStart().getLine(), "unknown variable " + ID);
        }
    }

    @Override
    public void exitAdd(HolyJavaParser.AddContext context) {
        final var value1 = stack.pop();
        final var value2 = stack.pop();

        if (!value1.type.equals(value2.type)) {
            error(context.getStart().getLine(), "add type mismatch");
        }

        if (value1.type == PrimitiveType.INT) {
            LLVMGenerator.add_i32(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.add_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.REAL));
        }
    }

    @Override
    public void exitMult(HolyJavaParser.MultContext context) {
        final var value1 = stack.pop();
        final var value2 = stack.pop();

        if (!value1.type.equals(value2.type)) {
            error(context.getStart().getLine(), "add type mismatch");
        }

        if (value1.type == PrimitiveType.INT) {
            LLVMGenerator.mult_i32(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.mult_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.REAL));
        }
    }

    @Override
    public void exitInt(HolyJavaParser.IntContext context) {
        stack.push(new Value(context.INT().getText(), PrimitiveType.INT));
    }

    @Override
    public void exitReal(HolyJavaParser.RealContext context) {
        stack.push(new Value(context.REAL().getText(), PrimitiveType.REAL));
    }

    @Override
    public void exitToint(HolyJavaParser.TointContext context) {
        final var value = stack.pop();
        LLVMGenerator.fptosi(value.name);
        stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.INT));
    }

    @Override
    public void exitToreal(HolyJavaParser.TorealContext context) {
        final var value = stack.pop();
        LLVMGenerator.sitofp(value.name);
        stack.push(new Value("%" + (LLVMGenerator.reg - 1), PrimitiveType.REAL));
    }

    void error(int line, String message) {
        System.err.println("Error, line " + line + ", " + message);
        System.exit(1);
    }

}
