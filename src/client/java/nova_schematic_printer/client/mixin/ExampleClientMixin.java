
// @name: ExampleClientMixin
// @type: Java Client Mixin
// @location: src/client/java/nova_schematic_printer/client/mixin/ExampleClientMixin.java

package nova_schematic_printer.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ExampleClientMixin {

    @Inject(
            at = @At("HEAD"),
            method = "run"
    )
    private void init(CallbackInfo info) {
        // Injected at the start of MinecraftClient.run().
    }
}
