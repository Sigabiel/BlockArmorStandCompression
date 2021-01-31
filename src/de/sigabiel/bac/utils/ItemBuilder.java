package de.sigabiel.bac.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	private ItemStack item;
	private ItemMeta iMeta;

	public ItemBuilder(Material material) {
		item = new ItemStack(material);
		iMeta = item.getItemMeta();
	}

	public ItemBuilder(Material material, int amount, short ID) {
		item = new ItemStack(material, amount, ID);
		iMeta = item.getItemMeta();
	}

	public ItemBuilder addName(String name) {
		iMeta.setDisplayName(name);
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment, int staerke, boolean b) {
		iMeta.addEnchant(enchantment, staerke, true);
		return this;
	}

	public ItemBuilder addLore(String lore, String lore2, String lore3) {
		String[] arr = new String[3];
		arr[0] = lore;
		arr[1] = lore2;
		arr[2] = lore3;
		iMeta.setLore(Arrays.asList(arr));
		return this;
	}

	public ItemStack setItem() {
		item.setItemMeta(iMeta);
		return item;
	}

}
