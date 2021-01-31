package de.sigabiel.bac.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import de.sigabiel.bac.CompressionArea;
import de.sigabiel.bac.Main;

public class SelectionHandler implements Listener {

	private HashMap<UUID, Integer> selecting = new HashMap<>();

	@EventHandler
	public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
		Entity en = e.getRightClicked();
		Player p = e.getPlayer();
		if (selecting.containsKey(p.getUniqueId()) && en.getType() == EntityType.ARMOR_STAND) {

			ArmorStand stand = (ArmorStand) en;
			if (stand.isCustomNameVisible() || stand.getCustomName() == null
					|| !stand.getCustomName().startsWith("bac")) {
				return;
			}

			int globalID = Integer.parseInt(stand.getCustomName().split(" ")[1]);

			ArrayList<ArmorStand> stands = new ArrayList<>();
			stands.add(stand);
			int range = selecting.get(p.getUniqueId());

			// scanning for entity that are part of the compression
			for (Entity nerabyEntity : en.getNearbyEntities(range, range, range)) {
				if (nerabyEntity.getType() == EntityType.ARMOR_STAND) {
					ArmorStand cacheStand = (ArmorStand) nerabyEntity;

					if (!cacheStand.isCustomNameVisible() && cacheStand.getCustomName() != null
							&& cacheStand.getCustomName().equals("bac " + globalID)) {
						stands.add(cacheStand);
					}

				}
			}

			p.sendMessage(Main.PREFIX + "Es das BAC wurde mit " + stands.size() + " Armorstands ausgewählt");
			selecting.remove(p.getUniqueId());
			Main.getInstance().addBAC(p.getUniqueId(), new CompressionArea(stands));

			e.setCancelled(true);
		}
	}

	public void addPlayer(UUID uuid, int range) {
		if (!selecting.containsKey(uuid)) {
			selecting.put(uuid, range);
		}
	}

}
