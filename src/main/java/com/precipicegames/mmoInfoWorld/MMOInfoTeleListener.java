package com.precipicegames.mmoInfoWorld;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMOInfoTeleListener extends PlayerListener {
	private MMOInfoWorld plugin;
	public MMOInfoTeleListener(MMOInfoWorld p){
		plugin = p;
	}
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.isCancelled())
			return;
		SpoutPlayer sp= SpoutManager.getPlayer(event.getPlayer());
		if(sp.isSpoutCraftEnabled())
		{
			plugin.updateDisplay(sp);
		}
	}

}
