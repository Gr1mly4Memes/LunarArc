package io.lunararcdevs.lunararc.common.server;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Redirects calls to Bukkit/Paper methods that plugins were compiled against but the bundled
 * Paper API does not have, onto the equivalent method that exists. Applied to plugin bytecode
 * when it is loaded.
 */
public final class LunarArcApiShims {
    private record Target(String name, String desc) {}

    /** "owner.name desc" of the missing method to the replacement on the same owner. */
    private static final Map<String, Target> REDIRECTS = Map.of(
            "org/bukkit/entity/EntityType.getKeyOrThrow()Lorg/bukkit/NamespacedKey;",
            new Target("getKey", "()Lorg/bukkit/NamespacedKey;"));

    private static final byte[][] MARKERS = {
            "getKeyOrThrow".getBytes(StandardCharsets.UTF_8)
    };

    private LunarArcApiShims() {}

    public static byte[] apply(byte[] bytecode) {
        if (!mentionsShimmedMethod(bytecode)) return bytecode;
        try {
            ClassReader reader = new ClassReader(bytecode);
            ClassWriter writer = new ClassWriter(0);
            boolean[] changed = {false};
            reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM9, delegate) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc, boolean isInterface) {
                            Target target = REDIRECTS.get(owner + "." + methodName + methodDesc);
                            if (target != null) {
                                changed[0] = true;
                                super.visitMethodInsn(opcode, owner, target.name(), target.desc(), isInterface);
                            } else {
                                super.visitMethodInsn(opcode, owner, methodName, methodDesc, isInterface);
                            }
                        }
                    };
                }
            }, 0);
            return changed[0] ? writer.toByteArray() : bytecode;
        } catch (RuntimeException ignored) {
            return bytecode;
        }
    }

    private static boolean mentionsShimmedMethod(byte[] bytecode) {
        for (byte[] marker : MARKERS) {
            outer:
            for (int i = 0; i <= bytecode.length - marker.length; i++) {
                for (int j = 0; j < marker.length; j++) {
                    if (bytecode[i + j] != marker[j]) continue outer;
                }
                return true;
            }
        }
        return false;
    }
}
