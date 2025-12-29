package com.example.exile_respawn;

import net.neoforged.fml.common.Mod;
import net.minecraft.world.level.GameRules;

@Mod(ExileRespawn.MODID)
public class ExileRespawnGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> RANDOM_RESPAWN = GameRules.register(
            "randomRespawn", // /gamerule 名
            GameRules.Category.PLAYER, // 分類（UI用）
            GameRules.BooleanValue.create(true) // 初期値
    );

    public static final GameRules.Key<GameRules.IntegerValue> RESPAWN_RADIUS = GameRules.register(
            "randomRespawnRadius",
            GameRules.Category.PLAYER,
            GameRules.IntegerValue.create(10000));

    // 明示的 init は不要（static 初期化で登録される）
}
