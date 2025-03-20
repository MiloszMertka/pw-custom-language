package pl.edu.pw.ee.llvm;

import pl.edu.pw.ee.HolyJavaBaseListener;
import pl.edu.pw.ee.HolyJavaParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

class LLVMActions extends HolyJavaBaseListener {

    private final Set<String> variables = new HashSet<>();

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
    public void exitWrite(HolyJavaParser.WriteContext ctx) {
        String ID = ctx.ID().getText();
        if (variables.contains(ID)) {
            LLVMGenerator.printf(ID);
        } else {
            System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable: " + ID);
        }
    }

    @Override
    public void exitAssign(HolyJavaParser.AssignContext ctx) {
        String ID = ctx.ID().getText();
        String INT = ctx.INT().getText();
        if (!variables.contains(ID)) {
            variables.add(ID);
            LLVMGenerator.declare(ID);
        }
        LLVMGenerator.assign(ID, INT);
    }

    @Override
    public void exitRead(HolyJavaParser.ReadContext ctx) {
        String ID = ctx.ID().getText();
        if (!variables.contains(ID)) {
            variables.add(ID);
            LLVMGenerator.declare(ID);
        }
        LLVMGenerator.scanf(ID);
    }

}
