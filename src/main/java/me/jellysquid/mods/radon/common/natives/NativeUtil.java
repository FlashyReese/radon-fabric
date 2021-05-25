package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.*;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.ref.Cleaner;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NativeUtil {
    protected static final LibraryLookup LibZSTD;
    protected static final LibraryLookup LibLMDB;
    protected static final Cleaner CLEANER = Cleaner.create();

    protected static MethodHandle getHandle(LibraryLookup library, String method, @Nullable Type returnType, Type... args) {
        var linker = CLinker.getInstance();
        var symbol = library.lookup(method);
        if (symbol.isEmpty()) {
            throw new NativeLibraryException("Couldn't find method "+method);
        }

        if (returnType == null) {
            return linker.downcallHandle(
                    symbol.get(),
                    MethodType.methodType(void.class, Arrays.stream(args).map(t -> t.javaRepresentation).collect(Collectors.toList())),
                    FunctionDescriptor.ofVoid(Arrays.stream(args).map(t -> t.layout).toArray(MemoryLayout[]::new))
            );
        } else {
            return linker.downcallHandle(
                    symbol.get(),
                    MethodType.methodType(returnType.javaRepresentation, Arrays.stream(args).map(t -> t.javaRepresentation).collect(Collectors.toList())),
                    FunctionDescriptor.of(returnType.layout, Arrays.stream(args).map(t -> t.layout).toArray(MemoryLayout[]::new))
            );
        }
    }

    /**
     * When a unsigned int is converted from C to Java a part of the values are now interpreted as negative number.
     * We can get around this by storing the int as a Java long instead.
     * <p>
     * Illustration:
     * <pre>
     * C integers     ################
     * Java integers  ########--------
     * Java longs     ################----------------
     * </pre></p>
     */
    protected static long Uint2Long(int i) {
        return Integer.toUnsignedLong(i);
    }

    /**
     * Converts a java long back to an int.
     * @see #Uint2Long(int)
     */
    protected static int Long2Uint(long i) {
        return (int)i;
    }

    /**
     * Copies a C-style null terminated string into a java string.
     * This copies the chars so may not be the most efficient algorithm
     * @param pointer pointer to the char array
     * @return a Java {@link String}
     */
    protected static String pointerToString(MemoryAddress pointer) {
        return CLinker.toJavaStringRestricted(pointer, Charset.defaultCharset());
    }

    /**
     * Creates a GC'd memory segment
     */
    public static MemorySegment allocateNative(long size) {
        return MemorySegment.allocateNative(size).registerCleaner(CLEANER);
    }

    /**
     * Creates a GC'd memory segment
     */
    public static MemorySegment allocateNative(MemoryLayout layout) {
        return MemorySegment.allocateNative(layout).registerCleaner(CLEANER);
    }

    static {
        LibZSTD = LibraryLookup.ofLibrary("zstd");
        LibLMDB = LibraryLookup.ofLibrary("lmdb");
    }

    /**
     * A single int that is allocated in memory and can be accessed via a pointer
     */
    public static class IntBuf {
        private final static VarHandle handle = MemoryLayouts.JAVA_INT.varHandle(int.class);
        private final MemorySegment segment;

        public IntBuf() {
            this.segment = MemorySegment.allocateNative(MemoryLayouts.JAVA_INT);
        }

        public MemoryAddress getPointer() {
            return segment.address();
        }

        public int get() {
            return (int)handle.get(segment);
        }

        public void set(int i) {
            handle.set(segment, i);
        }
    }

    /**
     * A pointer that is allocated in memory and can be accessed via a pointer
     */
    public static class PointerBuf implements AutoCloseable{
        private final MemorySegment segment;

        public PointerBuf() {
            this.segment = MemorySegment.allocateNative(CLinker.C_POINTER);
        }

        /**
         * @return a pointer that points to a pointer
         */
        public MemoryAddress getPointer() {
            return segment.address();
        }

        /**
         * @return the pointer that is in the internal memory segment
         */
        public MemoryAddress extract() {
            var objPointer = MemoryAccess.getAddress(segment);
            return objPointer;
        }

        @Override
        public void close() {
            segment.close();
        }
    }
}
