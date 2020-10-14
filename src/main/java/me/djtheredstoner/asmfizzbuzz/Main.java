package me.djtheredstoner.asmfizzbuzz;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

public class Main {

    static boolean output = Boolean.parseBoolean(System.getProperty("output", "false"));

    public static void main(String[] args) {
        ClassNode fizzBuzzClass = new ClassNode();
        fizzBuzzClass.name = "FizzBuzzClass";
        fizzBuzzClass.version = V1_8;
        fizzBuzzClass.access = ACC_PUBLIC;
        fizzBuzzClass.superName = "java/lang/Object";

        MethodNode mainMethod = new MethodNode(ACC_PUBLIC | ACC_STATIC, "fizzBuzz", "()V", null, null);
        InsnList list = new InsnList();

        LabelNode start = new LabelNode();
        list.add(start);
        list.add(new InsnNode(ICONST_1));
        list.add(new VarInsnNode(ISTORE, 0));

        LabelNode loopStart = new LabelNode();

        LabelNode returnLabel = new LabelNode();

        //Loop Start
        list.add(loopStart);
        list.add(new VarInsnNode(ILOAD, 0));
        list.add(new IntInsnNode(BIPUSH, 100));
        list.add(new JumpInsnNode(IF_ICMPGT, returnLabel));


        //Check if divisible by 15
        LabelNode fizzAndBuzz = new LabelNode();

        list.add(new VarInsnNode(ILOAD, 0));
        list.add(new IntInsnNode(BIPUSH, 15));
        list.add(new InsnNode(IREM));
        list.add(new JumpInsnNode(IFEQ, fizzAndBuzz));


        //Check if divisible by 3
        LabelNode fizz = new LabelNode();

        list.add(new VarInsnNode(ILOAD, 0));
        list.add(new InsnNode(ICONST_3));
        list.add(new InsnNode(IREM));
        list.add(new JumpInsnNode(IFEQ, fizz));

        //Check if divisible by 5
        LabelNode buzz = new LabelNode();

        list.add(new VarInsnNode(ILOAD, 0));
        list.add(new InsnNode(ICONST_5));
        list.add(new InsnNode(IREM));
        list.add(new JumpInsnNode(IFEQ, buzz));

        LabelNode printInt = new LabelNode();

        //Otherwise Print number
        list.add(new VarInsnNode(ILOAD, 0));
        list.add(new JumpInsnNode(GOTO, printInt));

        LabelNode printString = new LabelNode();

        list.add(fizzAndBuzz);
        list.add(new LdcInsnNode("FizzBuzz"));
        list.add(new JumpInsnNode(GOTO, printString));

        list.add(fizz);
        list.add(new LdcInsnNode("Fizz"));
        list.add(new JumpInsnNode(GOTO, printString));

        list.add(buzz);
        list.add(new LdcInsnNode("Buzz"));
        list.add(new JumpInsnNode(GOTO, printString));

        LabelNode doneLoop = new LabelNode();

        list.add(printString);
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add(new InsnNode(SWAP));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        list.add(new JumpInsnNode(GOTO, doneLoop));

        list.add(printInt);
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add(new InsnNode(SWAP));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false));
        list.add(new JumpInsnNode(GOTO, doneLoop));

        list.add(doneLoop);
        list.add(new IincInsnNode(0, 1));
        list.add(new JumpInsnNode(GOTO, loopStart));

        list.add(returnLabel);
        list.add(new InsnNode(RETURN));

        mainMethod.instructions.insert(list);

        fizzBuzzClass.methods.add(mainMethod);

        mainMethod.localVariables.add(new LocalVariableNode("i", "I", "I", start, returnLabel, 0));

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        //write™
        fizzBuzzClass.accept(writer);
        byte[] bytes = writer.toByteArray();

        if(output) {
            File outputFile = new File("FizzBuzzClass.class");

            try {
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileOutputStream os = new FileOutputStream(outputFile)) {
                os.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Class fizzBuzz = new ByteClassloader().loadClass("FizzBuzzClass", bytes);

        try {
            fizzBuzz.getDeclaredMethod("fizzBuzz").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
}
