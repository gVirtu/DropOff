package scp002.quickstack.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import scp002.quickstack.render.RendererCubeTarget;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ByteBufUtilsExt extends ByteBufUtils {

  private final ByteBuf buf;

  public ByteBufUtilsExt(ByteBuf buf) {
    this.buf = buf;
  }

  public void writeRendererCubeTargets(List<RendererCubeTarget> rendererCubeTargets) {
    buf.writeInt(rendererCubeTargets.size());
    rendererCubeTargets.forEach(this::writeRendererCubeTarget);
  }

  public List<RendererCubeTarget> readRendererCubeTargets() {
    int targetsLen = buf.readInt();
    return IntStream.range(0, targetsLen).mapToObj(i -> readRendererCubeTarget()).collect(Collectors.toList());
  }

  private void writeRendererCubeTarget(RendererCubeTarget rendererCubeTarget) {
    buf.writeLong(rendererCubeTarget.getBlockPos().toLong());
    buf.writeInt(rendererCubeTarget.getColor());
  }

  private RendererCubeTarget readRendererCubeTarget() {
    BlockPos blockPos = BlockPos.fromLong(buf.readLong());
    int color = buf.readInt();
    return new RendererCubeTarget(blockPos, color);
  }
}
