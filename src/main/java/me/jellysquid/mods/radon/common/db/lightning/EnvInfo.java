package me.jellysquid.mods.radon.common.db.lightning;

import me.jellysquid.mods.radon.common.natives.Lmdb;

public class EnvInfo {
    public final long mapSize;

    public EnvInfo(Lmdb.MDB_envinfo info) {
        // TODO: implement other fields
        this.mapSize = info.me_mapsize();
    }
}
