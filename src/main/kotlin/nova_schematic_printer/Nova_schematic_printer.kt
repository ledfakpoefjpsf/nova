
// @name: Nova_schematic_printer
// @type: Kotlin ModInitializer
// @location: src/main/kotlin/nova_schematic_printer/Nova_schematic_printer.kt

package nova_schematic_printer

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Nova_schematic_printer : ModInitializer {

    const val MOD_ID: String = "nova_schematic_printer"

    private val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("Nova Schematic Printer initialized!")
        LOGGER.info("Nova Schematic Printer is ready.")
    }

    fun id(path: String): Identifier {
        return Identifier.of(MOD_ID, path)
    }
}
