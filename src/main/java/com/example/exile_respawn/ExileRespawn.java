package com.example.exile_respawn;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExileRespawn.MODID)
public class ExileRespawn {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exile_respawn";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExileRespawn(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("サーバ立ち上げ中だよ🛴");
    }

    /**
     * プレイヤーがリスポーンした直後、遠くにテレポートする。
     * 
     * @param event
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        RandomSource random = level.random;

        // 半径
        // TODO: ゲームルールから取得した値を使う
        int radius = level.getGameRules()
                .getRule(ExileRespawnGameRules.RESPAWN_RADIUS)
                .get();
        LOGGER.info("半径は{}だよ", radius);
        double baseRadius = 10000;
        double jitter = 3000; // 誤差を入れる
        double r = baseRadius + (random.nextDouble() * 2 - 1) * jitter;

        // 飛ばされる方向
        double theta = random.nextDouble() * Math.PI * 2;
        int x = (int) (player.blockPosition().getX() + r * Math.cos(theta));
        int z = (int) (player.blockPosition().getZ() + r * Math.sin(theta));

        // 地中・空中を避ける
        level.getChunk(x >> 4, z >> 4); // チャンクをロード
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        // テレポート
        player.teleportTo(x, y, z);
    }

    /**
     * ベット使用時 or リスポーン時にメッセージ表示
     * 
     * @param event
     */
    @SubscribeEvent
    public void onSleep(PlayerSetSpawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        // TODO: これいつ表示されるの？
        player.displayClientMessage(
                Component.literal("この世界ではベッドはスポーン地点にならない。"),
                false);
    }
}
