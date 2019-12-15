package scp002.quickstack.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistryEntry;
import scp002.quickstack.render.RendererCubeTarget;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PacketBufferExt extends PacketBuffer {

  public PacketBufferExt(PacketBuffer buf) {
    super(buf);
  }

  public <T extends IForgeRegistryEntry<T>> void writeRegistryIdArray(@Nonnull List<T> entries){
    writeInt(entries.size());
    entries.forEach(this::writeRegistryId);
  }

  public <T extends IForgeRegistryEntry<T>> List<T> readRegistryIdArray(){
    int size = readInt();
    List<T> list = new ArrayList<>();
    for (int value = 0; value < size; value++) {
      T t = readRegistryId();
      list.add(t);
    }
    return list;
  }

  public void writeRendererCubeTargets(List<RendererCubeTarget> rendererCubeTargets) {
    writeInt(rendererCubeTargets.size());
    rendererCubeTargets.forEach(this::writeRendererCubeTarget);
  }

  public List<RendererCubeTarget> readRendererCubeTargets() {
    int targetsLen = readInt();
    return IntStream.range(0, targetsLen).mapToObj(i -> readRendererCubeTarget()).collect(Collectors.toList());
  }

  private void writeRendererCubeTarget(RendererCubeTarget rendererCubeTarget) {
    writeLong(rendererCubeTarget.getBlockPos().toLong());
    writeInt(rendererCubeTarget.getColor());
  }

  private RendererCubeTarget readRendererCubeTarget() {
    BlockPos blockPos = BlockPos.fromLong(readLong());
    int color = readInt();
    return new RendererCubeTarget(blockPos, color);
  }
}
