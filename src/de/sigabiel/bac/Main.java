package de.sigabiel.bac;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import de.sigabiel.bac.cmds.BACCommand;
import de.sigabiel.bac.listener.MoveHandler;
import de.sigabiel.bac.listener.PlayerListener;
import de.sigabiel.bac.listener.SelectionHandler;

public class Main extends JavaPlugin {

	public static final String PERMISSION_CREATE_BAC = "builder.createbac";

	public static final String PREFIX = "§7[§cBAC§7] ";

	private static Main instance;

	private HashMap<UUID, Location[]> idleUsers = new HashMap<>();
	private HashMap<UUID, CompressionArea> bacs = new HashMap<>();
	private MoveHandler moveHandler;
	private SelectionHandler selectionHandler;

	private int globalIndex;

	@Override
	public void onEnable() {
		instance = this;
		System.out.println("[BAC] startet");

		getCommand("bac").setExecutor(new BACCommand());

		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(moveHandler = new MoveHandler(), this);
		getServer().getPluginManager().registerEvents(selectionHandler = new SelectionHandler(), this);

		// loaading global index for bac load and create part
		if (getConfig().contains("globalindex")) {
			globalIndex = getConfig().getInt("globalindex");
		}
	}

	@Override
	public void onDisable() {

		// make sure every armorstand that is currently edited will be invisible
		for (CompressionArea area : bacs.values()) {
			for (ArmorStand ar : area.getArmorStands()) {
				ar.setVisible(false);
			}
		}
		super.onDisable();
	}

	public boolean hasBAC(UUID uuid) {
		return bacs.containsKey(uuid);
	}

	public UUID getUUID(CompressionArea area) {
		Set<UUID> keys = bacs.keySet();

		for (UUID key : keys) {
			if (bacs.get(key).equals(area)) {
				return key;
			}
		}

		return null;
	}

	public CompressionArea getBAC(UUID uuid) {
		return bacs.get(uuid);
	}

	public void removeBAC(UUID uuid) {
		bacs.remove(uuid);
	}

	public void addBAC(UUID uuid, CompressionArea bac) {
		bacs.put(uuid, bac);
	}

	public HashMap<UUID, Location[]> getIdleUsers() {
		return idleUsers;
	}

	public MoveHandler getMoveHandler() {
		return moveHandler;
	}

	public SelectionHandler getSelectionHandler() {
		return selectionHandler;
	}

	public static Main getInstance() {
		return instance;
	}

	public int getGlobalIndex() {
		return globalIndex;
	}

	public void addGlobalIndex() {
		globalIndex++;
		getConfig().set("globalindex", globalIndex);
		saveConfig();
	}

}
