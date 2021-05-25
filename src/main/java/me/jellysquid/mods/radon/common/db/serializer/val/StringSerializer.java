package me.jellysquid.mods.radon.common.db.serializer.val;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.ValueSerializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringSerializer implements ValueSerializer<String> {
    @Override
    public MemorySegment serialize(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
        buf.put(data);

        return MemorySegment.ofByteBuffer(buf);
    }

    @Override
    public String deserialize(MemorySegment input) {
        return StandardCharsets.UTF_8.decode(input.asByteBuffer())
                .toString();
    }
}
