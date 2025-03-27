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
    private static final int BUFFER_SIZE = 128;
    private final Map<String, Value> variables = new HashMap<>();
    private final Stack<Value> stack = new Stack<>();
    private final Stack<Array> arrayStack = new Stack<>();

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
    public void exitAssignarray(HolyJavaParser.AssignarrayContext context) {
        final var ID = context.ID().getText();
        final var array = variables.get(ID);

        if (array == null) {
            error(context.getStart().getLine(), "unknown array " + ID);
        }

        final var value = stack.pop();
        final var index = stack.pop();

        if (index.type != PrimitiveType.INT && index.type != PrimitiveType.LONG) {
            error(context.getStart().getLine(), "array index must be int or long");
        }

        if (index.type == PrimitiveType.INT) {
            LLVMGenerator.sext_i32(index.name);
        }

        if (value.type != array.type) {
            error(context.getStart().getLine(), "array type mismatch");
        }

        LLVMGenerator.assign_array_item(array.name, array.length, index.name, value.name, value.type.llvmType());
    }

    @Override
    public void exitAssign(HolyJavaParser.AssignContext context) {
        final var ID = context.ID().getText();
        final var variable = stack.pop();

        if (!variables.containsKey(ID)) {
            variables.put(ID, variable);

            if (variable instanceof Array) {
                return;
            }

            if (variable.type == PrimitiveType.INT) {
                LLVMGenerator.declare_i32(ID);
            }

            if (variable.type == PrimitiveType.LONG) {
                LLVMGenerator.declare_i64(ID);
            }

            if (variable.type == PrimitiveType.FLOAT) {
                LLVMGenerator.declare_float(ID);
            }

            if (variable.type == PrimitiveType.DOUBLE) {
                LLVMGenerator.declare_double(ID);
            }

            if (variable.type == PrimitiveType.STRING) {
                LLVMGenerator.declare_string(ID);
            }
        }

        if (variable instanceof Array) {
            return;
        }

        if (variable.type == PrimitiveType.INT) {
            LLVMGenerator.assign_i32(ID, variable.name);
        }

        if (variable.type == PrimitiveType.LONG) {
            LLVMGenerator.assign_i64(ID, variable.name);
        }

        if (variable.type == PrimitiveType.FLOAT) {
            LLVMGenerator.assign_float(ID, variable.name);
        }

        if (variable.type == PrimitiveType.DOUBLE) {
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
                case LONG -> LLVMGenerator.printf_i64(ID);
                case FLOAT -> LLVMGenerator.printf_float(ID);
                case DOUBLE -> LLVMGenerator.printf_double(ID);
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
        final var value = new Value(ID, PrimitiveType.STRING, BUFFER_SIZE - 1);
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

        if (value1.type == PrimitiveType.LONG) {
            LLVMGenerator.add_i64(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.LONG));
        }

        if (value1.type == PrimitiveType.FLOAT) {
            LLVMGenerator.add_float(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.FLOAT));
        }

        if (value1.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.add_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.DOUBLE));
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

        if (value1.type == PrimitiveType.LONG) {
            LLVMGenerator.sub_i64(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.LONG));
        }

        if (value1.type == PrimitiveType.FLOAT) {
            LLVMGenerator.sub_float(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.FLOAT));
        }

        if (value1.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.sub_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.DOUBLE));
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

        if (value1.type == PrimitiveType.LONG) {
            LLVMGenerator.mult_i64(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.LONG));
        }

        if (value1.type == PrimitiveType.FLOAT) {
            LLVMGenerator.mult_float(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.FLOAT));
        }

        if (value1.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.mult_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.DOUBLE));
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

        if (value1.type == PrimitiveType.LONG) {
            LLVMGenerator.div_i64(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.LONG));
        }

        if (value1.type == PrimitiveType.FLOAT) {
            LLVMGenerator.div_float(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.FLOAT));
        }

        if (value1.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.div_double(value1.name, value2.name);
            stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.DOUBLE));
        }
    }

    @Override
    public void enterArray(HolyJavaParser.ArrayContext context) {
        final var id = "arr" + (LLVMGenerator.arr - 1);
        final var array = new Array(id, PrimitiveType.UNKNOWN, 0);
        arrayStack.push(array);
        LLVMGenerator.arr++;
    }

    @Override
    public void exitArray(HolyJavaParser.ArrayContext context) {
        final var array = arrayStack.pop();
        LLVMGenerator.declare_array(array.name, array.length, array.type.llvmType());

        for (var index = 0; index < array.length; index++) {
            final var value = array.values.get(index);
            LLVMGenerator.assign_array_item(array.name, array.length, String.valueOf(index), value.name, value.type.llvmType());
        }

        if (context.getParent() instanceof HolyJavaParser.ArrayitemContext) {
            return;
        }

        stack.push(array);
    }

    @Override
    public void exitTofloat(HolyJavaParser.TofloatContext context) {
        final var value = stack.pop();

        if (value.type == PrimitiveType.DOUBLE) {
            return;
        }

        if (value.type == PrimitiveType.FLOAT) {
            LLVMGenerator.fptrunc_double(value.name);
        }

        if (value.type == PrimitiveType.INT) {
            LLVMGenerator.sitofp_int32_float(value.name);
        }

        if (value.type == PrimitiveType.LONG) {
            LLVMGenerator.sitofp_i64_float(value.name);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.FLOAT));
    }

    @Override
    public void exitToint(HolyJavaParser.TointContext context) {
        final var value = stack.pop();

        if (value.type == PrimitiveType.INT) {
            return;
        }

        if (value.type == PrimitiveType.LONG) {
            LLVMGenerator.trunc_i64(value.name);
        }

        if (value.type == PrimitiveType.FLOAT) {
            LLVMGenerator.fptosi_float_i32(value.name);
        }

        if (value.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.fptosi_double_i32(value.name);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.INT));
    }

    @Override
    public void exitTolong(HolyJavaParser.TolongContext context) {
        final var value = stack.pop();

        if (value.type == PrimitiveType.LONG) {
            return;
        }

        if (value.type == PrimitiveType.INT) {
            LLVMGenerator.sext_i32(value.name);
        }

        if (value.type == PrimitiveType.FLOAT) {
            LLVMGenerator.fptosi_float_i64(value.name);
        }

        if (value.type == PrimitiveType.DOUBLE) {
            LLVMGenerator.fptosi_double_i64(value.name);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.LONG));
    }

    @Override
    public void exitTodouble(HolyJavaParser.TodoubleContext context) {
        final var value = stack.pop();

        if (value.type == PrimitiveType.DOUBLE) {
            return;
        }

        if (value.type == PrimitiveType.FLOAT) {
            LLVMGenerator.fpext_float(value.name);
        }

        if (value.type == PrimitiveType.INT) {
            LLVMGenerator.sitofp_i32_double(value.name);
        }

        if (value.type == PrimitiveType.LONG) {
            LLVMGenerator.sitofp_i64_double(value.name);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), PrimitiveType.DOUBLE));
    }

    @Override
    public void exitArrayitem(HolyJavaParser.ArrayitemContext context) {
        final var value = stack.pop();
        final var array = arrayStack.peek();
        array.values.add(value);
        array.length++;

        if (array.type == PrimitiveType.UNKNOWN) {
            array.type = value.type;
            return;
        }

        if (array.type != value.type) {
            error(context.getStart().getLine(), "array type mismatch");
        }
    }

    @Override
    public void exitArrayvalue(HolyJavaParser.ArrayvalueContext context) {
        final var array = variables.get(context.ID().getText());

        if (array == null) {
            error(context.getStart().getLine(), "unknown array " + context.ID().getText());
        }

        final var index = stack.pop();

        if (index.type != PrimitiveType.INT && index.type != PrimitiveType.LONG) {
            error(context.getStart().getLine(), "array index must be int or long");
        }

        if (index.type == PrimitiveType.INT) {
            LLVMGenerator.sext_i32(index.name);
        }

        LLVMGenerator.load_array_value(array.name, array.length, index.name, array.type.llvmType());
        stack.push(new Value("%" + (LLVMGenerator.register - 1), array.type));
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
            case LONG -> LLVMGenerator.load_i64(ID);
            case FLOAT -> LLVMGenerator.load_float(ID);
            case DOUBLE -> LLVMGenerator.load_double(ID);
            case STRING -> LLVMGenerator.load_string(ID);
        }

        stack.push(new Value("%" + (LLVMGenerator.register - 1), value.type, value.length));
    }

    @Override
    public void exitFloat(HolyJavaParser.FloatContext context) {
        final var text = context.FLOAT().getText();
        final var id = text.substring(0, text.length() - 1);
        stack.push(new Value(id, PrimitiveType.FLOAT));
    }

    @Override
    public void exitInt(HolyJavaParser.IntContext context) {
        final var text = context.INT().getText();
        final var id = text.substring(0, text.length() - 1);
        stack.push(new Value(id, PrimitiveType.INT));
    }

    @Override
    public void exitLong(HolyJavaParser.LongContext context) {
        stack.push(new Value(context.LONG().getText(), PrimitiveType.LONG));
    }

    @Override
    public void exitDouble(HolyJavaParser.DoubleContext context) {
        stack.push(new Value(context.DOUBLE().getText(), PrimitiveType.DOUBLE));
    }

    @Override
    public void exitString(HolyJavaParser.StringContext context) {
        final var tmp = context.STRING().getText();
        final var content = tmp.substring(1, tmp.length() - 1);
        LLVMGenerator.constant_string(content);
        final var id = "str" + (LLVMGenerator.str - 1);
        stack.push(new Value(id, PrimitiveType.STRING, content.length()));
    }

    private void error(int line, String message) {
        final var errorMessage = "Error, line " + line + ", " + message;
        throw new IllegalStateException(errorMessage);
    }

}
