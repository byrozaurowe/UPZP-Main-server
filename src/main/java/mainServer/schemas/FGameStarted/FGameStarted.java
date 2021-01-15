// automatically generated by the FlatBuffers compiler, do not modify

package mainServer.schemas.FGameStarted;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class FGameStarted extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static FGameStarted getRootAsFGameStarted(ByteBuffer _bb) { return getRootAsFGameStarted(_bb, new FGameStarted()); }
  public static FGameStarted getRootAsFGameStarted(ByteBuffer _bb, FGameStarted obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public FGameStarted __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String ip() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer ipAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer ipInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public int port() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createFGameStarted(FlatBufferBuilder builder,
      int ipOffset,
      int port) {
    builder.startTable(2);
    FGameStarted.addPort(builder, port);
    FGameStarted.addIp(builder, ipOffset);
    return FGameStarted.endFGameStarted(builder);
  }

  public static void startFGameStarted(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addIp(FlatBufferBuilder builder, int ipOffset) { builder.addOffset(0, ipOffset, 0); }
  public static void addPort(FlatBufferBuilder builder, int port) { builder.addInt(1, port, 0); }
  public static int endFGameStarted(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }
  public static void finishFGameStartedBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedFGameStartedBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public FGameStarted get(int j) { return get(new FGameStarted(), j); }
    public FGameStarted get(FGameStarted obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}
