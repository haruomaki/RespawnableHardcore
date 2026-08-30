package com.example.exile_respawn;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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

    /**
     * Teleports the player away from their death location immediately upon respawning.
     * 
     * @param event The player respawn event
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // `.serverLevel()` has been removed in Minecraft 1.21.6 and later.
        var level = (ServerLevel) player.level();
        RandomSource random = level.random;

        // Teleport the player if the world is not hardcore and the custom gamerule is enabled
        if (!level.getLevelData().isHardcore() && level.getGameRules().get(ExileRespawnGameRules.ENABLED.get())) {
            // Configuration rules for calculation
            int radius = level.getGameRules().get(ExileRespawnGameRules.RADIUS.get());
            int looseness = level.getGameRules().get(ExileRespawnGameRules.LOOSENESS.get());
            double distance = radius + (random.nextDouble() * 2 - 1) * looseness;

            // Target coordinates logic
            int deathX = deathPos.getX();
            int deathY = deathPos.getY();
            int deathZ = deathPos.getZ();
            double theta = random.nextDouble() * Math.PI * 2;
            int x = (int) (deathX + distance * Math.cos(theta));
            int z = (int) (deathZ + distance * Math.sin(theta));

            // Force load the chunk to find a safe surface height
            level.getChunk(x >> 4, z >> 4);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            // Log details and execute teleportation
            LOGGER.info("Exile Respawn! (Radius: {}, Looseness: {})", radius, looseness);
            LOGGER.info(String.format("Death Position: (%d, %d, %d), distance: %.1f, theta: %.1f°", deathX, deathY, deathZ, distance, theta * 180 / Math.PI));
            LOGGER.info("Respawn Position: ({}, {}, {})", x, y, z);
            player.teleportTo(x, y, z);
        }
    }
}
