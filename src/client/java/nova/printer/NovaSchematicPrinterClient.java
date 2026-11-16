
 // @name: NovaSchematicPrinterClient
 // @type: Java Client Initializer
 // @location: src/client/java/nova/printer/NovaSchematicPrinterClient.java

package nova.printer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NovaSchematicPrinterClient implements ClientModInitializer {

    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {

        KeyBinding.Category category = KeyBinding.Category.register(
                Identifier.of("nova_schematic_printer", "main")
        );

        openKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.nova_schematic_printer.open",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        category
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new PrinterScreen());
                }
            }
        });
    }

    public static void message(String text) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal("[Nova Printer] " + text),
                    false
            );
        }
    }
}
