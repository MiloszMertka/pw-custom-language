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

    Map<String, VarType> variables = new HashMap<>();
    Stack<Value> stack = new Stack<>();

    @Override
    public void exitProg(HolyJavaParser.ProgContext ctx) {
        try {
            final var outputPath = Path.of("output.ll");
            final var llvmCode = LLVMGenerator.generate();
            Files.writeString(outputPath, llvmCode);
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    @Override
    public void exitAssign(HolyJavaParser.AssignContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        variables.put(ID, v.type);
        if (v.type == VarType.INT) {
            LLVMGenerator.declare_i32(ID);
            LLVMGenerator.assign_i32(ID, v.name);
        }
        if (v.type == VarType.REAL) {
            LLVMGenerator.declare_double(ID);
            LLVMGenerator.assign_double(ID, v.name);
        }
    }

    @Override
    public void exitPrint(HolyJavaParser.PrintContext ctx) {
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == VarType.INT) {
                LLVMGenerator.printf_i32(ID);
            }
            if (type == VarType.REAL) {
                LLVMGenerator.printf_double(ID);
            }
        } else {
            error(ctx.getStart().getLine(), "unknown variable " + ID);
        }
    }

    @Override
    public void exitAdd(HolyJavaParser.AddContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type == v2.type) {
            if (v1.type == VarType.INT) {
                LLVMGenerator.add_i32(v1.name, v2.name);
                stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.INT));
            }
            if (v1.type == VarType.REAL) {
                LLVMGenerator.add_double(v1.name, v2.name);
                stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.REAL));
            }
        } else {
            error(ctx.getStart().getLine(), "add type mismatch");
        }
    }

    @Override
    public void exitMult(HolyJavaParser.MultContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type == v2.type) {
            if (v1.type == VarType.INT) {
                LLVMGenerator.mult_i32(v1.name, v2.name);
                stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.INT));
            }
            if (v1.type == VarType.REAL) {
                LLVMGenerator.mult_double(v1.name, v2.name);
                stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.REAL));
            }
        } else {
            error(ctx.getStart().getLine(), "mult type mismatch");
        }
    }

    @Override
    public void exitInt(HolyJavaParser.IntContext ctx) {
        stack.push(new Value(ctx.INT().getText(), VarType.INT));
    }

    @Override
    public void exitReal(HolyJavaParser.RealContext ctx) {
        stack.push(new Value(ctx.REAL().getText(), VarType.REAL));
    }

    @Override
    public void exitToint(HolyJavaParser.TointContext ctx) {
        Value v = stack.pop();
        LLVMGenerator.fptosi(v.name);
        stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.INT));
    }

    @Override
    public void exitToreal(HolyJavaParser.TorealContext ctx) {
        Value v = stack.pop();
        LLVMGenerator.sitofp(v.name);
        stack.push(new Value("%" + (LLVMGenerator.reg - 1), VarType.REAL));
    }

    void error(int line, String msg) {
        System.err.println("Error, line " + line + ", " + msg);
        System.exit(1);
    }

}
