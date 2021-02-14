package de.sigabiel.bac.listener;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.sigabiel.bac.CompressionArea;
import de.sigabiel.bac.Main;

public class PlayerListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (Main.getInstance().getIdleUsers().containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			if (Main.getInstance().getIdleUsers().get(p.getUniqueId())[0] == null) {
				Main.getInstance().getIdleUsers().get(p.getUniqueId())[0] = e.getBlock().getLocation();

				p.sendMessage(Main.PREFIX + "First position was set!");
			} else {
				Main.getInstance().getIdleUsers().get(p.getUniqueId())[1] = e.getBlock().getLocation();

				p.sendMessage(Main.PREFIX + "Second position was set!");
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
				p.sendMessage(Main.PREFIX + "You've configured you BAC successfully");

				Location[] locs = Main.getInstance().getIdleUsers().get(p.getUniqueId());
				CompressionArea bac = new CompressionArea(locs[0], locs[1]);

				Main.getInstance().addBAC(p.getUniqueId(), bac);
				Main.getInstance().getIdleUsers().remove(p.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			ArmorStand stand = (ArmorStand) e.getRightClicked();
			if (stand.getCustomName() != null && stand.getCustomName().startsWith("bac")) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (Main.getInstance().hasBAC(e.getPlayer().getUniqueId())) {
			Main.getInstance().removeBAC(e.getPlayer().getUniqueId());
		}
	}

}
