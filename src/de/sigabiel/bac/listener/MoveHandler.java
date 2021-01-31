package de.sigabiel.bac.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.sigabiel.bac.CompressionMove;
import de.sigabiel.bac.Main;

public class MoveHandler implements Listener {

	private HashMap<UUID, CompressionMove> currentMove = new HashMap<>();

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (currentMove.containsKey(e.getPlayer().getUniqueId())) {
			currentMove.remove(e.getPlayer().getUniqueId()).stopMove(e.getPlayer());
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (onClick(e.getItem(), e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
		if (onClick(e.getPlayer().getItemInHand(), e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (onClick(e.getCurrentItem(), (Player) e.getWhoClicked())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if (currentMove.containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	public boolean onClick(ItemStack item, Player p) {
		if (currentMove.containsKey(p.getUniqueId())) {
			currentMove.get(p.getUniqueId()).clicked(item, p);
			return true;
		}
		return false;
	}

	public boolean isMoving(UUID uuid) {
		return currentMove.containsKey(uuid);
	}

	public void startMoving(Player p) {
		CompressionMove move = new CompressionMove(Main.getInstance().getBAC(p.getUniqueId()), p);
		currentMove.put(p.getUniqueId(), move);
	}

	public void stopMoving(Player p) {
		if (currentMove.containsKey(p.getUniqueId())) {
			currentMove.get(p.getUniqueId()).stopMove(p);
			currentMove.remove(p.getUniqueId());
		}
	}

}
