
// @name: PrinterScreen
// @type: Java Client Screen
// @location: src/client/java/nova/printer/PrinterScreen.java

package nova.printer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public final class PrinterScreen extends Screen {

    private String fileLabel = "No schematic loaded";
    private String status = "Select a schematic to begin.";

    private SchematicData loaded;

    private int offsetX;
    private int offsetY;
    private int offsetZ;

    private int rotation;
    private boolean mirrorX;
    private boolean replaceAir;

    public PrinterScreen() {
        super(Text.literal("Nova Schematic Printer"));
    }

    @Override
    protected void init() {

        int center = width / 2;
        int y = height / 2 - 85;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("LOAD SCHEMATIC"),
                        button -> loadFile()
                ).dimensions(center - 100, y, 200, 22).build()
        );

        y += 29;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("ROTATE 90°"),
                        button -> rotation = (rotation + 1) % 4
                ).dimensions(center - 100, y, 97, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("MIRROR X"),
                        button -> mirrorX = !mirrorX
                ).dimensions(center + 3, y, 97, 20).build()
        );

        y += 27;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("X -"),
                        button -> offsetX--
                ).dimensions(center - 100, y, 47, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("X +"),
                        button -> offsetX++
                ).dimensions(center - 49, y, 47, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Y -"),
                        button -> offsetY--
                ).dimensions(center + 3, y, 47, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Y +"),
                        button -> offsetY++
                ).dimensions(center + 54, y, 46, 20).build()
        );

        y += 25;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Z -"),
                        button -> offsetZ--
                ).dimensions(center - 100, y, 47, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Z +"),
                        button -> offsetZ++
                ).dimensions(center - 49, y, 47, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("AIR: SKIP"),
                        button -> {
                            replaceAir = !replaceAir;

                            button.setMessage(
                                    Text.literal(
                                            replaceAir
                                                    ? "AIR: REPLACE"
                                                    : "AIR: SKIP"
                                    )
                            );
                        }
                ).dimensions(center + 3, y, 97, 20).build()
        );

        y += 29;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("PRINT INSTANTLY"),
                        button -> print()
                ).dimensions(center - 100, y, 200, 25).build()
        );

        y += 32;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("CLOSE"),
                        button -> close()
                ).dimensions(center - 100, y, 200, 20).build()
        );
    }

    private void loadFile() {

        try {

            JFileChooser chooser = new JFileChooser();

            chooser.setDialogTitle("Select Minecraft schematic");

            chooser.setFileFilter(
                    new FileNameExtensionFilter(
                            "Sponge schematic (*.schem)",
                            "schem"
                    )
            );

            if (chooser.showOpenDialog(null)
                    != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();

            loaded = SchematicReader.read(file.toPath());

            fileLabel = file.getName();

            status = "Loaded "
                    + loaded.width + " x "
                    + loaded.height + " x "
                    + loaded.length;

        } catch (Exception exception) {

            loaded = null;

            status = "Load error: " + exception.getMessage();
        }
    }

    private void print() {

        MinecraftClient client = MinecraftClient.getInstance();

        if (loaded == null) {
            status = "Load a schematic first.";
            return;
        }

        if (client.player == null || client.world == null) {
            status = "Enter a world first.";
            return;
        }

        MinecraftServer server = client.getServer();

        if (server == null) {
            status = "Singleplayer printing only. Multiplayer needs server support.";
            return;
        }

        SchematicData data = loaded;

        int baseX = (int) Math.floor(client.player.getX()) + offsetX;
        int baseY = (int) Math.floor(client.player.getY()) + offsetY;
        int baseZ = (int) Math.floor(client.player.getZ()) + offsetZ;

        int turns = rotation;
        boolean mirror = mirrorX;
        boolean air = replaceAir;

        var dimension = client.world.getRegistryKey();

        status = "Printing...";

        server.execute(() -> {

            ServerWorld level = server.getWorld(dimension);

            if (level == null) {
                client.execute(() -> status = "Could not find target world.");
                return;
            }

            int placed = 0;
            int skipped = 0;

            int minY = level.getBottomY();
            int maxY = minY + level.getDimension().height();

            for (int y = 0; y < data.height; y++) {

                for (int z = 0; z < data.length; z++) {

                    for (int x = 0; x < data.width; x++) {

                        BlockEntry entry = data.blocks[
                                (y * data.length + z) * data.width + x
                        ];

                        if (entry == null) {
                            skipped++;
                            continue;
                        }

                        if (!air && entry.state().isAir()) {
                            skipped++;
                            continue;
                        }

                        int mx = mirror
                                ? data.width - 1 - x
                                : x;

                        int rx;
                        int rz;

                        switch (turns) {

                            case 1 -> {
                                rx = data.length - 1 - z;
                                rz = mx;
                            }

                            case 2 -> {
                                rx = data.width - 1 - mx;
                                rz = data.length - 1 - z;
                            }

                            case 3 -> {
                                rx = z;
                                rz = data.width - 1 - mx;
                            }

                            default -> {
                                rx = mx;
                                rz = z;
                            }
                        }

                        int px = baseX + rx;
                        int py = baseY + y;
                        int pz = baseZ + rz;

                        if (py < minY || py >= maxY) {
                            skipped++;
                            continue;
                        }

                        BlockPos position = new BlockPos(px, py, pz);

                        if (level.setBlockState(
                                position,
                                entry.state(),
                                3
                        )) {
                            placed++;
                        } else {
                            skipped++;
                        }
                    }
                }
            }

            int finalPlaced = placed;
            int finalSkipped = skipped;

            client.execute(() -> {
                status = "Finished! Placed "
                        + finalPlaced
                        + " | Skipped "
                        + finalSkipped;
            });
        });
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        renderBackground(context, mouseX, mouseY, delta);

        int center = width / 2;

        context.drawCenteredTextWithShadow(
                textRenderer,
                "NOVA SCHEMATIC PRINTER",
                center,
                height / 2 - 119,
                0x55DDFF
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                fileLabel,
                center,
                height / 2 - 105,
                0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                "Offset: X " + offsetX
                        + " Y " + offsetY
                        + " Z " + offsetZ
                        + " | Rotation: " + (rotation * 90) + "°",
                center,
                height / 2 - 93,
                0xBBDDEE
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                status,
                center,
                height / 2 + 116,
                0xFFE08A
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
