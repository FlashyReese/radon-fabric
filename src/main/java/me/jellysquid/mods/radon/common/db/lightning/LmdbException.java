package me.jellysquid.mods.radon.common.db.lightning;


import me.jellysquid.mods.radon.common.natives.Lmdb;

public class LmdbException extends RuntimeException {
    private final int code;

    public LmdbException(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return Lmdb.mdb_strerror(this.code);
    }
}
