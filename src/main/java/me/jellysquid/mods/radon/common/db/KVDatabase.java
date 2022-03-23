package me.jellysquid.mods.radon.common.db;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.DefaultSerializers;
import me.jellysquid.mods.radon.common.db.serializer.KeySerializer;
import me.jellysquid.mods.radon.common.db.serializer.ValueSerializer;
import me.jellysquid.mods.radon.common.db.spec.DatabaseSpec;
import me.jellysquid.mods.radon.common.io.compression.StreamCompressor;
import me.jellysquid.mods.radon.common.db.lightning.Dbi;
import me.jellysquid.mods.radon.common.db.lightning.Env;
import me.jellysquid.mods.radon.common.db.lightning.Txn;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.lmdb.LMDB;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KVDatabase<K, V> {
    private final LMDBInstance storage;

    private final Env env;
    private final Dbi dbi;

    private final KeySerializer<K> keySerializer;
    private final ValueSerializer<V> valueSerializer;

    private final StreamCompressor compressor;

    public KVDatabase(LMDBInstance storage, DatabaseSpec<K, V> spec) {
        this.storage = storage;

        this.env = this.storage.env();
        this.dbi = this.env.openDbi(spec.getNameAsCString(), LMDB.MDB_CREATE);

        this.keySerializer = DefaultSerializers.getKeySerializer(spec.getKeyType());
        this.valueSerializer = DefaultSerializers.getValueSerializer(spec.getValueType());
        this.compressor = spec.getCompressor();
    }

    public V getValue(K key) {
        ReentrantReadWriteLock lock = this.storage.getLock();
        lock.readLock()
                .lock();

        try {
            var buf = this.dbi.get(this.env.txnRead(), this.getKeyBuffer(key));

            if (buf == null) {
                return null;
            }

            MemorySegment decompressed;

            try {
                decompressed = this.compressor.decompress(buf);
            } catch (Exception e) {
                throw new RuntimeException("Failed to decompress value", e);
            }

            try {
                return this.valueSerializer.deserialize(decompressed);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize value", e);
            }
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    private MemorySegment getKeyBuffer(K key) {
        return this.keySerializer.serializeKey(key);
    }

    public KeySerializer<K> getKeySerializer() {
        return this.keySerializer;
    }

    public ValueSerializer<V> getValueSerializer() {
        return this.valueSerializer;
    }

    public StreamCompressor getCompressor() {
        return this.compressor;
    }

    public void putValue(Txn txn, K key, MemorySegment value) {
        this.dbi.put(txn, this.getKeyBuffer(key), value, 0);
    }

    public void close() {
        this.dbi.close();
    }
}
