package jp.haruomaki.exile_respawn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "respawn", at = @At("HEAD"))
    private void exileRespawn$debugRespawn(ServerPlayer player, boolean keepInventory, CallbackInfoReturnable<ServerPlayer> cir) {
        System.out.println("[ExileRespawn] PlayerList#respawn reached at HEAD");
        System.out.println("[ExileRespawn] player=" + player.getScoreboardName() + ", keepInventory=" + keepInventory);
    }
}