package me.jellysquid.mods.radon.common.db.lightning;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.natives.Lmdb;
import org.lwjgl.util.lmdb.LMDB;

public class Dbi {
    private final Env env;
    private final int dbi;

    public Dbi(Env env, int dbi) {
        this.env = env;
        this.dbi = dbi;
    }

    public void put(Txn txn, MemorySegment keyBuf, MemorySegment valueBuf, int flags) {
        try (Lmdb.MDB_val key = new Lmdb.MDB_val();
             Lmdb.MDB_val value = new Lmdb.MDB_val()) {
            key.setValue(keyBuf);
            value.setValue(valueBuf);

            LmdbUtil.checkError(Lmdb.mdb_put(txn, this.dbi, key, value, flags));
        }
    }

    public void close() {
        Lmdb.mdb_dbi_close(this.env.address(), this.dbi);
    }

    public MemorySegment get(Txn txn, MemorySegment keyBuf) {
        try (Lmdb.MDB_val key = new Lmdb.MDB_val();
             Lmdb.MDB_val value = new Lmdb.MDB_val()) {
            key.setValue(keyBuf);

            int result = Lmdb.mdb_get(txn, this.dbi, key, value);

            if (result == LMDB.MDB_NOTFOUND) {
                return null;
            }

            LmdbUtil.checkError(result);

            return value.getValue();
        }
    }
}
