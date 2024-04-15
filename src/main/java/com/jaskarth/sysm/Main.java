package com.jaskarth.sysm;

import com.jaskarth.sysm.parse.Tokenize;
import com.jaskarth.sysm.write.Writer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final String TEXT = """
            package pkg;
                        
            import java.lang.Object;
            import java.lang.String;
            import java.io.PrintStream;
            import java.lang.System;
                        
            public class Test {
            	version 61;
            	sourcefile "Test.java";
                        
            	public void <init>() {
            		code {
            		L0:
            			aload 0;
            			invokespecial void Object.<init>();
            			return;
            		L1:
            		}
                        
            		vars {
            			stack 1;
            			locals 1;
                        
            			local 0 (L0 to L1) Test this;
            		}
                        
            		lines {
            			line L0 -> 3;
            		}
            	}
                        
            	public void test(String) {
            		code {
            		L0:
            			getstatic PrintStream System.out;
            			aload 1;
            			invokevirtual void PrintStream.println(String);
            		L1:
            			return;
            		L2:
            		}
                        
            		vars {
            			stack 2;
            			locals 2;
                        
            			local 0 (L0 to L2) Test this;
            			local 1 (L0 to L2) String s;
            		}
                        
            		lines {
            			line L0 -> 5;
            			line L1 -> 6;
            		}
            	}
            }
            """;

		 public static void main(String[] args) throws Exception {
//        ClassReader cr = new ClassReader(Files.readAllBytes(Path.of("testData/TestClassFields.class")));
        ClassReader cr = new ClassReader(Files.readAllBytes(Path.of("testData/Test.class")));
        ClassNode node = new ClassNode();
        cr.accept(node, 0);

        System.out.println(Writer.write(node));

        System.out.println(Tokenize.tokenize(TEXT));
    }
}
