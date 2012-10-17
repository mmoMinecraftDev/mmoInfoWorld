/*
 * This file is part of mmoInfoWorld <http://github.com/mmoMinecraftDev/mmoInfoWorld>.
 *
 * mmoInfoWorld is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of mmoMinecraft (https://github.com/mmoMinecraftDev).
 *
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info.mmoInfoWorld;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.util.EnumBitSet;
import mmo.Core.MMOPlugin;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMOInfoWorld extends MMOPlugin
implements Listener
{
	private HashMap<SpoutPlayer, GenericLabel> worldLabels;
	private HashMap<String, String> NameMap;

	public EnumBitSet mmoSupport(EnumBitSet support)
	{
		support.set(MMOPlugin.Support.MMO_AUTO_EXTRACT);		
		return support;
	}
	
	public void onEnable()
	{
		super.onEnable();
		this.worldLabels = new HashMap();
		getServer().getPluginManager().registerEvents(this, this);
	}
	@EventHandler
	public void onMMOInfo(MMOInfoEvent event) {
		if (event.isToken("world")) {
			event.setWidget(this.plugin, updateDisplay(event.getPlayer()));
			event.setIcon("world.png");
			}		
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;
		SpoutPlayer sp = SpoutManager.getPlayer(event.getPlayer());
		if (sp.isSpoutCraftEnabled()) updateDisplay(sp); 
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(event.getPlayer());
		if (sp.isSpoutCraftEnabled()) updateDisplay(sp); 
	}

	public String getWorldName(Location l)
	{
		if (this.NameMap == null) {
			return l.getWorld().getName();
		}
		String alias = (String)this.NameMap.get(l.getWorld().getName());
		return alias != null ? alias : l.getWorld().getName();
	}

	public void loadConfiguration(FileConfiguration cfg)
	{
		this.NameMap = new HashMap();
		Set <String>keys = cfg.getKeys(true);
		if (keys != null) {
			for (String key : keys) {				
				if (key.startsWith("world-alias.")) {
					String alias = cfg.getString(key);
					if (alias == null) continue; this.NameMap.put(key.substring(key.indexOf('.') + 1), alias);
				}
			}
		} else {
			HashMap defaultlist = new HashMap();
			defaultlist.put("world", "World");
			cfg.set("world-alias", defaultlist);
			loadConfiguration(cfg);
		}
	}

	public Widget updateDisplay(SpoutPlayer player) {
		GenericLabel text = (GenericLabel)this.worldLabels.get(player);
		text = text == null ? (GenericLabel)new GenericLabel().setResize(true).setFixed(true) : text;
		if ((text.getText() == null) || (!text.getText().equalsIgnoreCase(getWorldName(player.getLocation())))) {
			text.setText(": "+getWorldName(player.getLocation()));			
			text.setDirty(true);
		}
		this.worldLabels.put(player, text);
		return text;
	}
}