package me.jellysquid.mods.radon.common.db.serializer.val;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.Scannable;
import me.jellysquid.mods.radon.common.db.serializer.ValueSerializer;
import me.jellysquid.mods.radon.common.io.ByteBufferInputStream;
import me.jellysquid.mods.radon.common.io.ByteBufferOutputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.scanner.NbtScanner;

import java.io.*;
import java.nio.ByteBuffer;

public class NbtCompoundSerializer implements ValueSerializer<NbtCompound>, Scannable<NbtScanner> {
    @Override
    public MemorySegment serialize(NbtCompound value) throws IOException {
        if (value == null) {
            return MemorySegment.ofByteBuffer(ByteBuffer.allocateDirect(0).flip()); // Fixme: Don't do this
        }
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


    @Override
    public void scan(MemorySegment input, NbtScanner scanner) throws IOException  {
        try (DataInputStream dataInput = new DataInputStream(new ByteBufferInputStream(input.asByteBuffer()))) {
            NbtIo.scan(dataInput, scanner);
        }
    }
}
