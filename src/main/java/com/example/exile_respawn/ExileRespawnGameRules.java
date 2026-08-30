package com.example.exile_respawn;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExileRespawnGameRules {
    public static final DeferredRegister<GameRule<?>> GAME_RULES = DeferredRegister.create(Registries.GAME_RULE, ExileRespawn.MODID);

    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> EXILE_RESPAWN = GAME_RULES.register("exile_respawn", id -> new GameRule<>(
            GameRuleCategory.PLAYER,
            GameRuleType.BOOL,
            BoolArgumentType.bool(),
            (visitor, rule) -> visitor.visitBoolean(rule),
            Codec.BOOL,
            value -> value ? 1 : 0,
            true,
            FeatureFlagSet.of()));

    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> EXILE_RESPAWN_RADIUS = GAME_RULES.register("exile_respawn_radius", id -> new GameRule<>(
            GameRuleCategory.PLAYER,
            GameRuleType.INT,
            IntegerArgumentType.integer(0),
            (visitor, rule) -> visitor.visitInteger(rule),
            Codec.INT,
            Integer::intValue,
            10000,
            FeatureFlagSet.of()));

    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> EXILE_RESPAWN_LOOSENESS = GAME_RULES.register("exile_respawn_looseness", id -> new GameRule<>(
            GameRuleCategory.PLAYER,
            GameRuleType.INT,
            IntegerArgumentType.integer(0),
            (visitor, rule) -> visitor.visitInteger(rule),
            Codec.INT,
            Integer::intValue,
            3000,
            FeatureFlagSet.of()));

    public static void register(IEventBus modBus) {
        GAME_RULES.register(modBus);
    }
}