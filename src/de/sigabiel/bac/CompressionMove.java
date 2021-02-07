package de.sigabiel.bac;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.sigabiel.bac.utils.EnumDirection;
import de.sigabiel.bac.utils.ItemBuilder;

public class CompressionMove {

	private CompressionArea area;
	private ItemStack[] hotbarCache;
	private boolean advanced;
	private int selectionIndex;

	public CompressionMove(CompressionArea area, Player p) {
		this.area = area;

		area.setMove(true);
		hotbarCache = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			if (p.getInventory().getItem(i) != null) {
				hotbarCache[i] = p.getInventory().getItem(i);
			}

			p.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		p.getInventory().setItem(22, new ItemStack(Material.AIR));

		int i = 1;
		for (EnumDirection dir : EnumDirection.values()) {
			p.getInventory().setItem(i, new ItemBuilder(Material.PRISMARINE_SHARD).addName("Pos " + dir.name()).setItem());
			i++;
		}
		p.getInventory().setItem(8, new ItemBuilder(Material.PRISMARINE_CRYSTALS).addName("§cSwitch mode").setItem());

	}

	public void clicked(ItemStack item, Player p) {
		if (item != null && item.hasItemMeta()) {

			if (item.getItemMeta().getDisplayName().equals("§cSwitch mode")) {

				if (advanced) {
					int i = 1;
					for (EnumDirection dir : EnumDirection.values()) {
						p.getInventory().setItem(i, new ItemBuilder(Material.PRISMARINE_SHARD).addName("Pos " + dir.name()).setItem());
						i++;
					}

					p.getInventory().setItem(0, new ItemStack(Material.AIR));
					p.getInventory().setItem(7, new ItemStack(Material.AIR));
					p.getInventory().setItem(22, new ItemStack(Material.AIR));
					p.sendMessage(Main.PREFIX + "Du bist num im Global Modus");
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);

					area.getArmorStands().get(selectionIndex).setVisible(false);

				} else {
					p.getInventory().setItem(7, new ItemBuilder(Material.NAME_TAG).addName("§a>>").setItem());
					p.getInventory().setItem(0, new ItemBuilder(Material.NAME_TAG).addName("§a<<").setItem());
					p.getInventory().setItem(22, new ItemBuilder(Material.NAME_TAG).addName("§aChange Move mode").setItem());
					p.sendMessage(Main.PREFIX + "Du bist nun im Single Modus");
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);

					area.getArmorStands().get(selectionIndex).setVisible(true);
				}

				advanced = !advanced;
			} else if (item.getItemMeta().getDisplayName().startsWith("Pos")) {
				EnumDirection dir = EnumDirection.valueOf(item.getItemMeta().getDisplayName().split(" ")[1]);

				if (advanced) {
					area.changePosition(area.getArmorStands().get(selectionIndex), dir);
				} else {
					area.changeWholePosition(dir);
				}

			} else if (item.getItemMeta().getDisplayName().startsWith("Rot")) {
				if (!advanced) {
					return;
				}

				EnumDirection dir = EnumDirection.valueOf(item.getItemMeta().getDisplayName().split(" ")[1]);
				area.changeRotation(area.getArmorStands().get(selectionIndex), dir);

			} else if (item.getItemMeta().getDisplayName().equals("§aChange Move mode")) {
				if (!advanced) {
					return;
				}

				if (p.getInventory().getItem(2).getItemMeta().getDisplayName().startsWith("Pos")) {
					p.getInventory().setItem(1, new ItemStack(Material.AIR));
					p.getInventory().setItem(6, new ItemStack(Material.AIR));

					int i = 1;
					for (EnumDirection dir : EnumDirection.values()) {
						p.getInventory().setItem(i, new ItemBuilder(Material.STICK).addName("Rot " + dir.name()).setItem());
						i++;
					}
				} else {
					int i = 1;
					for (EnumDirection dir : EnumDirection.values()) {
						p.getInventory().setItem(i, new ItemBuilder(Material.PRISMARINE_SHARD).addName("Pos " + dir.name()).setItem());
						i++;
					}

				}
			} else if (item.getItemMeta().getDisplayName().equals("§a>>")) {
				area.getArmorStands().get(selectionIndex).setVisible(false);
				if ((area.getArmorStands().size() - 1) < (selectionIndex + 1)) {
					selectionIndex = 0;
					area.getArmorStands().get(selectionIndex).setVisible(true);
				} else {
					selectionIndex++;
					area.getArmorStands().get(selectionIndex).setVisible(true);
				}
			} else if (item.getItemMeta().getDisplayName().equals("§a<<")) {
				area.getArmorStands().get(selectionIndex).setVisible(false);

				if ((selectionIndex - 1) < 0) {
					selectionIndex = area.getArmorStands().size() - 1;
					area.getArmorStands().get(selectionIndex).setVisible(true);
				} else {
					selectionIndex--;
					area.getArmorStands().get(selectionIndex).setVisible(true);
				}
			}

		}
	}

	public void stopMove(Player p) {
		for (int i = 0; i < 9; i++) {
			if (hotbarCache[i] == null) {
				p.getInventory().setItem(i, new ItemStack(Material.AIR));
			} else {
				p.getInventory().setItem(i, hotbarCache[i]);
			}
		}

		p.getInventory().setItem(22, new ItemStack(Material.AIR));
		area.getArmorStands().get(selectionIndex).setVisible(false);
		area.setMove(false);
	}
}
