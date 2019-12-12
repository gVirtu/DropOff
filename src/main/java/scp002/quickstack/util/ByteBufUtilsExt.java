package scp002.quickstack.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import scp002.quickstack.render.RendererCubeTarget;

import java.awt.*;
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
        List<RendererCubeTarget> rendererCubeTargets;
        int targetsLen = buf.readInt();

        rendererCubeTargets = IntStream.range(0, targetsLen).mapToObj(i -> readRendererCubeTarget()).collect(Collectors.toList());

        return rendererCubeTargets;
    }

    private void writeRendererCubeTarget(RendererCubeTarget rendererCubeTarget) {
        writeBlockPos(rendererCubeTarget.getBlockPos());
        writeColor(rendererCubeTarget.getColor());
    }

    private RendererCubeTarget readRendererCubeTarget() {
        BlockPos blockPos = readBlockPos();
        Color color = readColor();

        return new RendererCubeTarget(blockPos, color);
    }

    private void writeBlockPos(BlockPos blockPos) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
    }

    private BlockPos readBlockPos() {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        return new BlockPos(x, y, z);
    }

    private void writeColor(Color color) {
        buf.writeInt(color.getRed());
        buf.writeInt(color.getGreen());
        buf.writeInt(color.getBlue());
    }

    private Color readColor() {
        int red = buf.readInt();
        int green = buf.readInt();
        int blue = buf.readInt();

        return new Color(red, green, blue);
    }

}
