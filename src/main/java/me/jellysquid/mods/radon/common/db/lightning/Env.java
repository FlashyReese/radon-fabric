package me.jellysquid.mods.radon.common.db.lightning;

import jdk.incubator.foreign.MemoryAddress;
import me.jellysquid.mods.radon.common.natives.CString;
import me.jellysquid.mods.radon.common.natives.Lmdb;
import me.jellysquid.mods.radon.common.natives.NativeUtil;

import java.io.File;

public class Env {
    private final MemoryAddress env;

    Env(MemoryAddress env) {
        this.env = env;
    }

    public static Builder builder() {
        try (var pointer = new NativeUtil.PointerBuf()) {
            LmdbUtil.checkError(Lmdb.mdb_env_create(pointer));

            return new Env.Builder(pointer.extract());
        }
    }

    public Dbi openDbi(CString name, int flags) {
        return LmdbUtil.transaction(this, (stack, txn) -> {
            var dbiHandle = new NativeUtil.IntBuf();
            LmdbUtil.checkError(Lmdb.mdb_dbi_open(txn, name, flags, dbiHandle));
            return new Dbi(this, dbiHandle.get());
        });
    }

    public EnvInfo getInfo() {
        Lmdb.MDB_envinfo info = new Lmdb.MDB_envinfo();
        LmdbUtil.checkError(Lmdb.mdb_env_info(this.env, info));

        return new EnvInfo(info);
    }

    public void setMapSize(long size) {
        LmdbUtil.checkError(Lmdb.mdb_env_set_mapsize(this.env, size));
    }

    public Txn txnWrite() {
        return this.txn(0);
    }

    public Txn txnRead() {
        return this.txn(0x20000); // LMDB.MDB_RDONLY
    }

    private Txn txn(int flags) {
        try (var txnPointer = new NativeUtil.PointerBuf()) {
            LmdbUtil.checkError(Lmdb.mdb_txn_begin(this.env, MemoryAddress.NULL, flags, txnPointer));

            return new Txn(txnPointer.extract());
        }
    }

    public void close() {
        Lmdb.mdb_env_close(this.env);
    }

    MemoryAddress address() {
        return this.env;
    }

    public static class Builder {
        private final MemoryAddress pointer;

        public Builder(MemoryAddress pointer) {
            this.pointer = pointer;
        }

        public Builder setMaxDatabases(int limit) {
            LmdbUtil.checkError(Lmdb.mdb_env_set_maxdbs(this.pointer, limit));

            return this;
        }

        public Env open(File file, int flags) {
            return this.open(new CString(file.getAbsolutePath()), flags);
        }

        public Env open(CString path, int flags) {
            LmdbUtil.checkError(Lmdb.mdb_env_open(this.pointer, path, flags, 0_664));

            return new Env(this.pointer);
        }
    }
}
