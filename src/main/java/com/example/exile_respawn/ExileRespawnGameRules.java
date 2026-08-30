package com.example.exile_respawn;

// import net.neoforged.bus.api.IEventBus;
// import net.neoforged.neoforge.event.RegisterGameRuleCategoryEvent;

// import com.mojang.brigadier.arguments.IntegerArgumentType;
// import com.mojang.serialization.Codec;

// import net.minecraft.core.Registry;
// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.network.chat.Component;
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.flag.FeatureFlagSet;
// import net.minecraft.world.level.gamerules.GameRule;
// import net.minecraft.world.level.gamerules.GameRuleCategory;
// import net.minecraft.world.level.gamerules.GameRuleType;
// import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
// import net.minecraft.world.level.gamerules.GameRules;

// @Mod(ExileRespawn.MODID)
// public class ExileRespawnGameRules {

//     public static final GameRules.Key<GameRules.BooleanValue> EXILE_RESPAWN = GameRules.register(
//             "exileRespawn", // /gamerule 名
//             GameRules.Category.PLAYER, // 分類（UI用）
//             GameRules.BooleanValue.create(true) // 初期値
//     );

//     public static final GameRules.Key<GameRules.IntegerValue> EXILE_RESPAWN_RADIUS = GameRules.register(
//             "exileRespawnRadius",
//             GameRules.Category.PLAYER,
//             GameRules.IntegerValue.create(10000));

//     public static final GameRules.Key<GameRules.IntegerValue> EXILE_RESPAWN_LOOSENESS = GameRules.register(
//             "exileRespawnLooseness",
//             GameRules.Category.PLAYER,
//             GameRules.IntegerValue.create(3000));

//     // 明示的 init は不要（static 初期化で登録される）

// }

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExileRespawnGameRules {
    public static final DeferredRegister<GameRule<?>> GAME_RULES = DeferredRegister.create(Registries.GAME_RULE, ExileRespawn.MODID);

    // Booleanの例
    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> EXILE_RESPAWN = GAME_RULES.register("exile_respawn", id -> GameRules.registerBoolean(
            id.getPath(), // または id.getPath() など状況による
            GameRuleCategory.PLAYER,
            true // デフォルト値
    ));

    // Integerの例
    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> EXILE_RESPAWN_RADIUS = GAME_RULES.register("exile_respawn_radius", id -> GameRules.registerInteger(
            id.getPath(),
            GameRuleCategory.PLAYER,
            10000, 0));

    // Integerの例
    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> EXILE_RESPAWN_LOOSENESS = GAME_RULES.register("exile_respawn_looseness", id -> GameRules.registerInteger(
            id.getPath(),
            GameRuleCategory.PLAYER,
            3000, 0));

    public static void register(IEventBus modBus) {
        GAME_RULES.register(modBus);
    }
}