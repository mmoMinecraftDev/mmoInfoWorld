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
package com.precipicegames.mmoInfoWorld;

import java.util.HashMap;
import mmo.Core.MMOPlugin;
import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOListener;

import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

@SuppressWarnings("deprecation")
public class MMOInfoWorld extends MMOPlugin {
	private HashMap<SpoutPlayer,GenericLabel> worldLabels;
	private HashMap<String,String> NameMap;

	@Override
	public void onEnable() {
		super.onEnable();
		worldLabels = new HashMap<SpoutPlayer,GenericLabel>();
		
		pm.registerEvent(Type.CUSTOM_EVENT,
				new MMOListener() {
					@Override
					public void onMMOInfo(MMOInfoEvent event) {
						if (event.isToken("world")) {
							event.setWidget(plugin, updateDisplay(event.getPlayer()));
						}
					}}, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, new MMOInfoTeleListener(this), Priority.Normal, plugin);
	}
	public String getWorldName(Location l)
	{
		if(NameMap == null)
			return l.getWorld().getName();
		String alias  = NameMap.get(l.getWorld().getName()); 
		return (alias != null) ? alias : l.getWorld().getName();
	}

	@Override
	public void loadConfiguration(Configuration cfg) {
		NameMap = new HashMap<String,String>();
		for(String key : cfg.getKeys("world-alias"))
		{
			String alias = cfg.getString("world-alias." + key);
			if(alias != null)
			{
				NameMap.put(key, alias);
			}
		}
	}
	public Widget updateDisplay(SpoutPlayer player) {
		GenericLabel text = worldLabels.get(player);
		text = (text == null) ? (GenericLabel) new GenericLabel().setResize(true).setFixed(true) : text;
		if(text.getText() == null || !text.getText().equalsIgnoreCase(getWorldName(player.getLocation())))
		{
			text.setText(getWorldName(player.getLocation()));
			text.setDirty(true);
		}
		worldLabels.put(player, text);
		return text;
	}
}
