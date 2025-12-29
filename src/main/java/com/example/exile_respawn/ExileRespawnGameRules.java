package com.example.exile_respawn;

import net.neoforged.fml.common.Mod;
import net.minecraft.world.level.GameRules;

@Mod(ExileRespawn.MODID)
public class ExileRespawnGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> EXILE_RESPAWN = GameRules.register(
            "exileRespawn", // /gamerule 名
            GameRules.Category.PLAYER, // 分類（UI用）
            GameRules.BooleanValue.create(true) // 初期値
    );

    public static final GameRules.Key<GameRules.IntegerValue> EXILE_RESPAWN_RADIUS = GameRules.register(
            "exileRespawnRadius",
            GameRules.Category.PLAYER,
            GameRules.IntegerValue.create(10000));

    public static final GameRules.Key<GameRules.IntegerValue> EXILE_RESPAWN_LOOSENESS = GameRules.register(
            "exileRespawnLooseness",
            GameRules.Category.PLAYER,
            GameRules.IntegerValue.create(3000));

    // 明示的 init は不要（static 初期化で登録される）
}
