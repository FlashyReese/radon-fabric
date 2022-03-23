package me.jellysquid.mods.radon.common.db;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.lightning.Txn;

import java.io.IOException;

public class KVTransaction<K, V> {
    private final KVDatabase<K, V> storage;
    private final Object2ReferenceMap<K, MemorySegment> pending = new Object2ReferenceOpenHashMap<>();

    public KVTransaction(KVDatabase<K, V> storage) {
        this.storage = storage;
    }

    public void add(K key, V value) {
        try {
            var data = this.storage.getValueSerializer()
                    .serialize(value);

            var compressedData = this.storage.getCompressor()
                    .compress(data);

            this.pending.put(key, compressedData);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't serialize value", e);
        }
    }

    void addChanges(Txn txn) {
        for (Object2ReferenceMap.Entry<K, MemorySegment> entry : this.pending.object2ReferenceEntrySet()) {
            this.storage.putValue(txn, entry.getKey(), entry.getValue());
        }
    }

    void clear() {
        this.pending.clear();
    }
}
