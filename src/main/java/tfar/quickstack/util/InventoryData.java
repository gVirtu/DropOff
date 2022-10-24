package tfar.quickstack.util;

import net.minecraft.core.BlockPos;

public class InventoryData {

    public final BlockPos pos;
    public boolean success = false;

    public InventoryData(BlockPos pos) {
        this.pos = pos;
    }

    public void setSuccessful() {
        success = true;
    }
}
