package jp.haruomaki.exile_respawn;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExileRespawn.MOD_ID)
public class ExileRespawn {
    // Define mod id in a common place for everything to reference
    // Read from gradle.properties instead; tried to eliminate this duplication, but gave up for now.
    public static final String MOD_ID = "exile_respawn";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

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
     * Changes the player's respawn position when Exile Respawn is enabled.
     * <p>
     * We keep the original respawn position as the center, then pick a random direction and distance and move the player away from it. The Y coordinate is adjusted to the surface height at the new location.
     * </p>
     * <p>
     * Hardcore worlds are intentionally left untouched.
     * </p>
     */
    @SubscribeEvent
    public void onPlayerRespawnPosition(PlayerRespawnPositionEvent event) {
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
            var transition = event.getTeleportTransition();
            var pos = transition.position();
            double theta = random.nextDouble() * Math.PI * 2;
            int x = (int) (pos.x + distance * Math.cos(theta));
            int z = (int) (pos.z + distance * Math.sin(theta));

            // Force load the chunk to find a safe surface height
            level.getChunk(x >> 4, z >> 4);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            var newPos = new Vec3(x, y, z);

            // Log details and execute teleportation
            LOGGER.info("Exile Respawn! (Radius: {}, Looseness: {})", radius, looseness);
            LOGGER.info(String.format("Original Respawn Position: (%.1f, %.1f, %.1f), distance: %.1f, theta: %.1f°", pos.x, pos.y, pos.z, distance, theta * 180 / Math.PI));
            LOGGER.info("Respawn Position: ({}, {}, {})", x, y, z);
            event.setTeleportTransition(new TeleportTransition(
                    level,
                    newPos,
                    transition.deltaMovement(),
                    transition.yRot(),
                    transition.xRot(),
                    transition.relatives(),
                    transition.postTeleportTransition()));
        }
    }
}
