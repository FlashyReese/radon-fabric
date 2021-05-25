package me.jellysquid.mods.radon.common.db.spec;

import me.jellysquid.mods.radon.common.io.compression.StreamCompressor;
import me.jellysquid.mods.radon.common.natives.CString;

public class DatabaseSpec<K, V> {
    private final String name;
    /**
     * Copy of {@link #name} encoded as a {@link CString} for performance reasons.
     */
    private final CString cName;

    private final Class<K> key;
    private final Class<V> value;

    private final StreamCompressor compressor;
    private final int initialSize;

    public DatabaseSpec(String name, Class<K> key, Class<V> value, StreamCompressor compressor, int initialSize) {
        this.name = name;
        this.cName = new CString(name);
        this.key = key;
        this.value = value;
        this.compressor = compressor;
        this.initialSize = initialSize;
    }

    public Class<K> getKeyType() {
        return this.key;
    }

    public Class<V> getValueType() {
        return this.value;
    }

    public StreamCompressor getCompressor() {
        return this.compressor;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @return this database's name as a C like null terminated string
     */
    public CString getNameAsCString() {
        return this.cName;
    }

    @Override
    public String toString() {
        return String.format("DatabaseSpec{key=%s, value=%s}@%s", this.key.getName(), this.value.getName(), this.hashCode());
    }

    public int getInitialSize() {
        return this.initialSize;
    }
}
