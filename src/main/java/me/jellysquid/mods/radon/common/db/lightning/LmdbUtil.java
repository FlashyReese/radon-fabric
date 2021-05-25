package me.jellysquid.mods.radon.common.db.lightning;

import jdk.incubator.foreign.MemoryAddress;
import me.jellysquid.mods.radon.common.natives.Lmdb;
import me.jellysquid.mods.radon.common.natives.NativeUtil;

public class LmdbUtil {
    public static <T> T transaction(Env env, Transaction<T> transaction) {
        T ret;

        try (var mutablePointer = new NativeUtil.PointerBuf()) {
            LmdbUtil.checkError(Lmdb.mdb_txn_begin(env.address(), MemoryAddress.NULL, 0, mutablePointer));
            MemoryAddress txn = mutablePointer.extract();

            int err;

            try {
                ret = transaction.exec(mutablePointer, txn);
                err = Lmdb.mdb_txn_commit(txn);
            } catch (Throwable t) {
                Lmdb.mdb_txn_abort(txn);
                throw t;
            }

            LmdbUtil.checkError(err);
        }

        return ret;
    }

    public static void checkError(int rc) {
        if (rc != Lmdb.MDB_SUCCESS) {
            throw new LmdbException(rc);
        }
    }
}
