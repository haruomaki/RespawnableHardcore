package jp.haruomaki.exile_respawn;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.LevelData;
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
     * Calculates a random exile spot away from the original spawn.
     * <p>
     * Picks a random direction and distance based on gamerules, then forces a chunk load to find a safe surface height (no suffocating!).
     * </p>
     *
     * @param level  The server level for world and gamerule data.
     * @param center The original spawn point we are pushing the player away from.
     * @return A safe ground-level Vec3 for the new exile location.
     */
    private Vec3 calculateExileRespawnPos(ServerLevel level, Vec3 center) {
        // Configuration rules for calculation
        RandomSource random = level.random;
        int radius = level.getGameRules().get(ExileRespawnGameRules.RADIUS.get());
        int looseness = level.getGameRules().get(ExileRespawnGameRules.LOOSENESS.get());
        double distance = radius + (random.nextDouble() * 2.0D - 1.0D) * looseness;

        // Target coordinates logic
        double theta = random.nextDouble() * Math.PI * 2.0D;
        int x = (int) (center.x + distance * Math.cos(theta));
        int z = (int) (center.z + distance * Math.sin(theta));

        // Force load the chunk to find a safe surface height
        level.getChunk(x >> 4, z >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        // Log details
        LOGGER.info("Exile Respawn! (Radius: {}, Looseness: {})", radius, looseness);
        LOGGER.info(String.format("Original Respawn Position: %s, distance: %.1f, theta: %.1f°", center.toString(), distance, theta * 180 / Math.PI));
        LOGGER.info("Respawn Position: ({}, {}, {})", x, y, z);

        return new Vec3(x, y, z);
    }

    /**
     * Hijacks the player's respawn to banish them to the exile zone.
     * <p>
     * Skips non-players and hardcore worlds. If the exile rule is on, it overrides the teleport destination and forces vanilla to lock this new position as their home sweet home.
     * </p>
     */
    @SubscribeEvent
    public void onPlayerRespawnPosition(PlayerRespawnPositionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // `.serverLevel()` has been removed in Minecraft 1.21.6 and later.
        var level = (ServerLevel) player.level();

        // Teleport the player if the world is not hardcore and the custom gamerule is enabled
        if (!level.getLevelData().isHardcore() && level.getGameRules().get(ExileRespawnGameRules.ENABLED.get())) {
            var transition = event.getTeleportTransition();
            var customRespawnPos = calculateExileRespawnPos(level, transition.position());

            // Override the respawn position
            event.setTeleportTransition(new TeleportTransition(
                    level,
                    customRespawnPos,
                    transition.deltaMovement(),
                    transition.yRot(),
                    transition.xRot(),
                    transition.relatives(),
                    transition.postTeleportTransition()));

            // Update vanilla respawn config to lock the new location
            BlockPos respawnPos = BlockPos.containing(customRespawnPos);
            GlobalPos globalPos = new GlobalPos(level.dimension(), respawnPos);
            LevelData.RespawnData respawnData = new LevelData.RespawnData(globalPos, 0.0F, 0.0F);
            ServerPlayer.RespawnConfig config = new ServerPlayer.RespawnConfig(respawnData, true);
            player.setRespawnPosition(config, true);
            player.displayClientMessage(Component.literal("§4[Exile Respawn] This is your new place...💀"), false);
        }
    }
}
