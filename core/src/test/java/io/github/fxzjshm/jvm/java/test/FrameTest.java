package io.github.fxzjshm.jvm.java.test;

import java.io.IOException;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.fxzjshm.jvm.java.classfile.ClassFile;
import io.github.fxzjshm.jvm.java.classfile.MemberInfo;
import io.github.fxzjshm.jvm.java.runtime.Frame;
import io.github.fxzjshm.jvm.java.runtime.Thread;
import io.github.fxzjshm.jvm.java.runtime.VM;
import io.github.fxzjshm.jvm.java.runtime.data.Method;


public class FrameTest {

    @Before
    public void init() throws IOException {
        if (ClassFileTest.classMap.isEmpty()){
            ClassFileTest cft=new ClassFileTest();
            cft.compileClass();
            cft.parseClass();
        }
    }

    @Test
    public void testExec() throws Throwable {
        ClassFile cf = ClassFileTest.classMap.get("Gauss");
        VM vm = new VM();
        if(cf != null) {
            for (MemberInfo methodInfo : cf.methods) {
                if (Objects.equals(methodInfo.name, "Gauss.main")) {
                    Method method = new Method(methodInfo, null);
                    Frame frame = new Frame(new Thread(vm), method);
                    while (frame.reader.available() != 0) frame.exec();
                    Assert.assertEquals(5050,frame.localVars.get(1));
                }
            }
        }
    }

}