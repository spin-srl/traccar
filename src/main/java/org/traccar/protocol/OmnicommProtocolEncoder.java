package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class OmnicommProtocolEncoder {
    public static ByteBuf encode(ByteBuf input) {
        input.resetReaderIndex();

        ByteBuf result = Unpooled.buffer();

        result.writeByte(input.readUnsignedByte());
        result.writeByte(input.readUnsignedByte());

        while (input.readableBytes() > 0) {
            short b = input.readUnsignedByte();

            if (b == 0xC0) {
                result.writeByte(0xDB);
                result.writeByte(0xDC);
            } else if (b == 0xDB) {
                result.writeByte(0xDB);
                result.writeByte(0xDD);
            } else {
                result.writeByte(b);
            }
        }

        return result;
    }
}
