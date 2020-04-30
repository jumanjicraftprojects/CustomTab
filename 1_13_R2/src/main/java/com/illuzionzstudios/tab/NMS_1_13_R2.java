package com.illuzionzstudios.tab;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMS_1_13_R2 implements NMSHandler {

    @Override
    public void setAvatar(int x, int y, Player player, Player... players) {
        this.setAvatar(x, y, ((CraftPlayer) player).getProfile(), players);
    }

    @Override
    public void addSkin(Player player, Player... players) {
        this.addSkin(player.getUniqueId(), ((CraftPlayer) player).getProfile(), players);
    }
}
