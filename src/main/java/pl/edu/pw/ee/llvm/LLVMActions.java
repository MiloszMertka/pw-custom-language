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
    static int BUFFER_SIZE = 128;
    private final Map<String, Value> variables = new HashMap<>();
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

        if (!variables.containsKey(ID)) {
            variables.put(ID, variable);

            if (variable.type == PrimitiveType.INT) {
                LLVMGenerator.declare_i32(ID);
            }

            if (variable.type == PrimitiveType.REAL) {
                LLVMGenerator.declare_double(ID);
            }

            if (variable.type == PrimitiveType.STRING) {
                LLVMGenerator.declare_string(ID);
            }
        }

        if (variable.type == PrimitiveType.INT) {
            LLVMGenerator.assign_i32(ID, variable.name);
        }

        if (variable.type == PrimitiveType.REAL) {
            LLVMGenerator.assign_double(ID, variable.name);
        }

        if (variable.type == PrimitiveType.STRING) {
            LLVMGenerator.assign_string(ID, variable.name);
        }
    }

    @Override
    public void exitPrint(HolyJavaParser.PrintContext context) {
        final var ID = context.ID().getText();
        if (variables.containsKey(ID)) {
            final var value = variables.get(ID);
            switch (value.type) {
                case INT -> LLVMGenerator.printf_i32(ID);
                case REAL -> LLVMGenerator.printf_double(ID);
                case STRING -> LLVMGenerator.printf_string(ID);
                case UNKNOWN -> error(context.getStart().getLine(), "unknown variable " + ID);
            }
        } else {
            error(context.getStart().getLine(), "unknown variable");
        }
    }

    @Override
    public void exitRead(HolyJavaParser.ReadContext context) {
        final var ID = context.ID().getText();
        Value value = new Value(ID, PrimitiveType.STRING, BUFFER_SIZE - 1);
        variables.put(ID, value);
        LLVMGenerator.scanf(ID, BUFFER_SIZE);
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
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.add_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.REAL));
        }

        if (value1.type == PrimitiveType.STRING) {
            LLVMGenerator.add_string(value1.name, value1.length, value2.name, value2.length);
            stack.push(new Value("%" + (LLVMGenerator.register - 3), PrimitiveType.STRING, value1.length + value2.length));
        }
    }

    @Override
    public void exitSub(HolyJavaParser.SubContext context) {
        final var value1 = stack.pop();
        final var value2 = stack.pop();

        if (!value1.type.equals(value2.type)) {
            error(context.getStart().getLine(), "sub type mismatch");
        }

        if (value1.type == PrimitiveType.INT) {
            LLVMGenerator.sub_i32(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.sub_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.REAL));
        }
    }

    @Override
    public void exitMult(HolyJavaParser.MultContext context) {
        final var value1 = stack.pop();
        final var value2 = stack.pop();

        if (!value1.type.equals(value2.type)) {
            error(context.getStart().getLine(), "mult type mismatch");
        }

        if (value1.type == PrimitiveType.INT) {
            LLVMGenerator.mult_i32(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.mult_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.REAL));
        }
    }

    @Override
    public void exitDiv(HolyJavaParser.DivContext context) {
        final var value1 = stack.pop();
        final var value2 = stack.pop();

        if (!value1.type.equals(value2.type)) {
            error(context.getStart().getLine(), "div type mismatch");
        }

        if (value1.type == PrimitiveType.INT) {
            LLVMGenerator.div_i32(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
        }

        if (value1.type == PrimitiveType.REAL) {
            LLVMGenerator.div_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.REAL));
        }
    }

    @Override
    public void exitToint(HolyJavaParser.TointContext context) {
        final var value = stack.pop();
        LLVMGenerator.fptosi(value.name);
        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
    }

    @Override
    public void exitToreal(HolyJavaParser.TorealContext context) {
        final var value = stack.pop();
        LLVMGenerator.sitofp(value.name);
        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.REAL));
    }

    @Override
    public void exitId(HolyJavaParser.IdContext context) {
        final var ID = context.ID().getText();

        if (!variables.containsKey(ID)) {
            error(context.getStart().getLine(), "unknown variable " + ID);
        }

        final var value = variables.get(ID);
        switch (value.type) {
            case INT -> LLVMGenerator.load_i32(ID);
            case REAL -> LLVMGenerator.load_double(ID);
            case STRING -> LLVMGenerator.load_string(ID);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), value.type, value.length));
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
    public void exitString(HolyJavaParser.StringContext context) {
        final var tmp = context.STRING().getText();
        final var content = tmp.substring(1, tmp.length() - 1);
        LLVMGenerator.constant_string(content);
        final var id = "ptrstr" + (LLVMGenerator.str - 1);
        stack.push(new Value(id, PrimitiveType.STRING, content.length()));
    }

    private void error(int line, String message) {
        final var errorMessage = "Error, line " + line + ", " + message;
        throw new IllegalStateException(errorMessage);
    }

}
