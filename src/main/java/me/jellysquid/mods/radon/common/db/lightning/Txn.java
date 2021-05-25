package me.jellysquid.mods.radon.common.db.lightning;

import jdk.incubator.foreign.MemoryAddress;
import me.jellysquid.mods.radon.common.natives.Lmdb;

public class Txn {
    private final MemoryAddress id;

    Txn(MemoryAddress pointer) {
        this.id = pointer;
    }

    public void commit() {
        LmdbUtil.checkError(Lmdb.mdb_txn_commit(this.id));
    }

    public void abort() {
        Lmdb.mdb_txn_abort(this.id);
    }

    public MemoryAddress raw() {
        return this.id;
    }
}
