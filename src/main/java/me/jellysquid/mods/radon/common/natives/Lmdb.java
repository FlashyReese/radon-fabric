package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.*;
import me.jellysquid.mods.radon.common.db.lightning.Txn;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Lmdb {
    private static final MethodHandle mdb_dbi_open = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_dbi_open",
            Type.UINT,
            Type.POINTER, Type.POINTER, Type.UINT, Type.POINTER
    );
    private static final MethodHandle mdb_txn_begin = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_txn_begin",
            Type.UINT,
            Type.POINTER, Type.POINTER, Type.UINT, Type.POINTER
    );
    private static final MethodHandle mdb_txn_commit = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_txn_commit",
            Type.UINT,
            Type.POINTER
    );
    private static final MethodHandle mdb_txn_abort = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_txn_abort",
            Type.UINT,
            Type.POINTER
    );
    private static final MethodHandle mdb_env_info = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_info",
            Type.UINT,
            Type.POINTER, Type.POINTER
    );
    private static final MethodHandle mdb_env_create = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_create",
            Type.UINT,
            Type.POINTER
    );
    private static final MethodHandle mdb_env_open = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_open",
            Type.UINT,
            Type.POINTER, Type.POINTER, Type.UINT, Type.UINT
    );
    private static final MethodHandle mdb_env_close = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_close",
            null,
            Type.POINTER
    );
    private static final MethodHandle mdb_env_set_maxdbs = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_set_maxdbs",
            Type.UINT,
            Type.POINTER, Type.UINT
    );
    private static final MethodHandle mdb_env_set_mapsize = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_env_set_mapsize",
            Type.UINT,
            Type.POINTER, Type.SIZE_T
    );
    private static final MethodHandle mdb_put = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_put",
            Type.UINT,
            Type.POINTER, Type.UINT, Type.POINTER, Type.POINTER, Type.UINT
    );
    private static final MethodHandle mdb_get = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_get",
            Type.UINT,
            Type.POINTER, Type.UINT, Type.POINTER, Type.POINTER
    );
    private static final MethodHandle mdb_dbi_close = NativeUtil.getHandle(
            NativeUtil.LibLMDB,
            "mdb_dbi_close",
            Type.UINT,
            Type.POINTER, Type.UINT
    );

    public static int mdb_dbi_open(MemoryAddress txn, CString name, int flags, NativeUtil.IntBuf dbi) {
        try {
            return (int)mdb_dbi_open.invokeExact(
                    txn,
                    name.getPointer(),
                    flags,
                    dbi.getPointer()
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_txn_begin(MemoryAddress env, MemoryAddress parent, int flags, NativeUtil.PointerBuf txnPointer) {
        try {
            return (int)mdb_txn_begin.invokeExact(
                    env,
                    parent,
                    flags,
                    txnPointer.getPointer()
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_txn_commit(MemoryAddress txn) {
        try {
            return (int)mdb_txn_commit.invokeExact(txn);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_txn_abort(MemoryAddress txn) {
        try {
            return (int)mdb_txn_abort.invokeExact(txn);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }


    public static int mdb_env_info(MemoryAddress env, MDB_envinfo stat) {
        try {
            return (int)mdb_env_info.invokeExact(env, stat.address());
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_env_create(NativeUtil.PointerBuf env) {
        try {
            return (int)mdb_env_create.invokeExact(env.getPointer());
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_env_open(MemoryAddress env, CString path, int flags, int mode) {
        try {
            return (int)mdb_env_open.invokeExact(env, path.getPointer(), flags, mode);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static void mdb_env_close(MemoryAddress env) {
        try {
            mdb_env_close.invokeExact(env);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_env_set_maxdbs(MemoryAddress env, int dbs) {
        try {
            return (int)mdb_env_set_maxdbs.invokeExact(env, dbs);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_env_set_mapsize(MemoryAddress env, long dbs) {
        try {
            return (int)mdb_env_set_mapsize.invokeExact(env, dbs);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_put(Txn txn, int dbi, MDB_val val, MDB_val key, int flags) {
        try {
            return (int)mdb_put.invokeExact(txn.raw(), dbi, val.address(), key.address(), flags);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_get(Txn txn, int dbi, MDB_val val, MDB_val key) {
        try {
            return (int)mdb_get.invokeExact(txn.raw(), dbi, val.address(), key.address());
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static int mdb_dbi_close(MemoryAddress env, int dbi) {
        try {
            return (int)mdb_dbi_close.invokeExact(env, dbi);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in lmdb", t);
        }
    }

    public static class MDB_envinfo implements Addressable{
        private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
                CLinker.C_POINTER.withName("me_mapaddr"),
                Type.SIZE_T.layout.withName("me_mapsize"),
                Type.SIZE_T.layout.withName("me_last_pgno"),
                Type.SIZE_T.layout.withName("me_mapsize"),
                CLinker.C_INT.withName("me_maxreaders"),
                CLinker.C_INT.withName("\tme_numreaders")
        );
        private static final VarHandle ME_MAPSIZE = LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("me_mapsize"));

        private final MemorySegment storage;

        public MDB_envinfo() {
            this.storage = NativeUtil.allocateNative(LAYOUT);
        }

        public MemoryAddress address() {
            return storage.address();
        }

        public long me_mapsize() {
            return (long)ME_MAPSIZE.get(storage);
        }
    }

    public static class MDB_val implements AutoCloseable, Addressable {
        private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
                Type.SIZE_T.layout.withName("mv_size"),
                Type.POINTER.layout.withName("mv_data")
        );
        private static final VarHandle MV_SIZE = LAYOUT.varHandle(Type.SIZE_T.javaRepresentation, MemoryLayout.PathElement.groupElement("mv_size"));
        private static final VarHandle MV_DATA = LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("mv_data"));

        private final MemorySegment storage = MemorySegment.allocateNative(LAYOUT);

        public MemoryAddress address() {
            return storage.address();
        }

        public void setValue(MemorySegment segment) {
            MV_DATA.set(this.storage, segment.address().toRawLongValue());
            MV_SIZE.set(this.storage, segment.byteSize());
        }

        public MemorySegment getValue() {
            var address = MemoryAddress.ofLong((long)MV_DATA.get(this.storage));
            var size = (long)MV_SIZE.get(this.storage);
            return address.asSegmentRestricted(size);
        }

        @Override
        public void close() {
            this.storage.close();
        }
    }
}
