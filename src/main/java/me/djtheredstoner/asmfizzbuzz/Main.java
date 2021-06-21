package me.djtheredstoner.asmfizzbuzz;

import me.djtheredstoner.asmdsl.InsnListBuilder;
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

        InsnListBuilder builder = new InsnListBuilder() {{
            add(L("start"));
            iconst_1();
            istore(0);

            //Loop Start
            add(L("loopStart"));
            iload(0);
            bipush(100);
            if_icmpgt(L("return"));

            //Check if divisible by 15
            iload(0);
            bipush(15);
            irem();
            ifeq(L("fizzAndBuzz"));

            //Check if divisible by 3
            iload(0);
            iconst_3();
            irem();
            ifeq(L("fizz"));

            //Check if divisible by 5
            iload(0);
            iconst_5();
            irem();
            ifeq(L("buzz"));

            //Otherwise Print number
            iload(0);
            _goto(L("printInt"));

            add(L("fizzAndBuzz"));
            ldc("FizzBuzz");
            _goto(L("printString"));

            add(L("fizz"));
            ldc("Fizz");
            _goto(L("printString"));

            add(L("buzz"));
            ldc("Buzz");
            _goto(L("printString"));

            add(L("printString"));
            getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
            swap();
            invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            _goto(L("doneLoop"));

            add(L("printInt"));
            getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
            swap();
            invokevirtual("java/io/PrintStream", "println", "(I)V");
            _goto(L("doneLoop"));

            add(L("doneLoop"));
            iinc(0, 1);
            _goto(L("loopStart"));

            add(L("return"));
            _return();
        }};

        mainMethod.instructions.insert(builder.l());

        fizzBuzzClass.methods.add(mainMethod);

        // TODO when there is public label api
        //mainMethod.localVariables.add(new LocalVariableNode("i", "I", "I", builder., returnLabel, 0));

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

        Class<?> fizzBuzz = new ByteClassloader().loadClass("FizzBuzzClass", bytes);

        try {
            fizzBuzz.getDeclaredMethod("fizzBuzz").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
}
