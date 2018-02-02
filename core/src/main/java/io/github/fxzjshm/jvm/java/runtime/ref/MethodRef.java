package io.github.fxzjshm.jvm.java.runtime.ref;

import java.util.Objects;

import io.github.fxzjshm.jvm.java.classfile.Bitmask;
import io.github.fxzjshm.jvm.java.classfile.cp.MemberRefInfo;
import io.github.fxzjshm.jvm.java.runtime.data.Class;
import io.github.fxzjshm.jvm.java.runtime.data.Method;
import io.github.fxzjshm.jvm.java.runtime.data.RuntimeConstantPool;

import static io.github.fxzjshm.jvm.java.runtime.ref.MethodRef.MethodLookup.lookupMethod;

public class MethodRef extends MemberRef {
    private Method method = null;

    public MethodRef(RuntimeConstantPool rcp, MemberRefInfo info) {
        super(rcp, info);
    }

    public Method resolvedMethod() {
        if (method == null) {
            resolveMethodRef();
        }
        return method;
    }

    private void resolveMethodRef() {
        Class d = rcp.clazz;
        Class c = resolvedClass();
        if ((c.classFile.accessFlags & Bitmask.ACC_INTERFACE) != 0) {
            throw new IncompatibleClassChangeError(c.classFile.name + " should not be an interface");
        }
        Method method = lookupMethod(c, name, descriptor);
        if (method == null) {
            throw new NoSuchMethodError("Cannot find " + className + '.' + name + '(' + descriptor + ')');
        }
        if (!Bitmask.isAccessibleTo(d, c, method.info.accessFlags)) {
            throw new IllegalAccessError("Cannot access " + c.classFile.name + '.' + method.info.name + "from" + d.classFile.name)
        }
    }

    private static class MethodLookup {
        private static Method lookupMethod(Class clazz, String name, String descriptor) {
            Method method = lookupMethodInClass(clazz, name, descriptor);
            if (method == null) {
                return lookupMethodInInterfaces(clazz, name, descriptor);
            } else {
                return method;
            }
        }

        private static Method lookupMethodInClass(Class clazz, String name, String descriptor) {
            for (Class c = clazz; c != null; c = c.superClass) {
                for (Method method : c.methods) {
                    if (Objects.equals(method.info.name, name) && Objects.equals(method.info.descriptor, descriptor)) {
                        return method;
                    }
                }
            }
            return null;
        }
    }
}
