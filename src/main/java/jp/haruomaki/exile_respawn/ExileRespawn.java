package jp.haruomaki.exile_respawn;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExileRespawn.MOD_ID)
public class ExileRespawn {
    // Define mod id in a common place for everything to reference
    // Read from gradle.properties instead; tried to eliminate this duplication, but gave up for now.
    public static final String MOD_ID = "exile_respawn";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private BlockPos deathPos;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExileRespawn(IEventBus modEventBus, ModContainer modContainer) {
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExileRespawn) to respond directly to events.
        NeoForge.EVENT_BUS.register(this);

        // Register the game rules.
        ExileRespawnGameRules.register(modEventBus);
    }

    /**
     * Records the exact coordinates where a player died.
     * 
     * @param event The living death event
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        deathPos = player.blockPosition();
    }

}
