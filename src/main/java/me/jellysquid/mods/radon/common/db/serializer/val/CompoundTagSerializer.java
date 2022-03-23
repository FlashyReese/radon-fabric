package me.jellysquid.mods.radon.common.db.serializer.val;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.ValueSerializer;
import me.jellysquid.mods.radon.common.io.ByteBufferInputStream;
import me.jellysquid.mods.radon.common.io.ByteBufferOutputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.*;
import java.nio.ByteBuffer;

public class CompoundTagSerializer implements ValueSerializer<NbtCompound> {
    @Override
    public MemorySegment serialize(NbtCompound value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);

        try (DataOutputStream out = new DataOutputStream(bytes)) {
            NbtIo.write(value, out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize NBT", e);
        }

        ByteBuffer buf = ByteBuffer.allocateDirect(bytes.size());
        bytes.writeTo(new ByteBufferOutputStream(buf));
        buf.flip();

        return MemorySegment.ofByteBuffer(buf);
    }

    @Override
    public NbtCompound deserialize(MemorySegment input) throws IOException {
        try (DataInputStream dataInput = new DataInputStream(new ByteBufferInputStream(input.asByteBuffer()))) {
            return NbtIo.read(dataInput);
        }
    }
}
