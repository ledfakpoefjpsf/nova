
package nova.printer;

// @name: SchematicData
// @type: Java class
// @location: src/main/java/nova/printer/SchematicData.java

import net.minecraft.block.BlockState;

public final class SchematicData {

    public final int width;
    public final int height;
    public final int length;

    public final BlockEntry[] blocks;

    public SchematicData(
            int width,
            int height,
            int length,
            BlockEntry[] blocks
    ) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
    }
}

record BlockEntry(BlockState state) {}
