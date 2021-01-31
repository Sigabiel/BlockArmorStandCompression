package de.sigabiel.bac.cmds;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.sigabiel.bac.CompressionArea;
import de.sigabiel.bac.Main;
import de.sigabiel.bac.utils.EnumBuildType;
import de.sigabiel.bac.utils.ItemBuilder;

public class BACCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission(Main.PERMISSION_CREATE_BAC)) {

				if (args.length < 1) {
					sendInstructions(p);
					return false;
				}

				if (args[0].equalsIgnoreCase("create")) {
					if (!Main.getInstance().getIdleUsers().containsKey(p.getUniqueId())) {

						if (Main.getInstance().hasBAC(p.getUniqueId())) {
							Main.getInstance().removeBAC(p.getUniqueId());
							p.sendMessage(Main.PREFIX + "Dein zuvor selektiertes BAC wurde gelöscht");
						}

						if (Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							Main.getInstance().getMoveHandler().stopMoving(p);
						}

						Main.getInstance().getIdleUsers().put(p.getUniqueId(), new Location[2]);
						p.sendMessage(Main.PREFIX + "Bitte schlage jetzt auf zwei Blöcke um den Bereich zu makieren");

					} else {
						p.sendMessage(Main.PREFIX + "§cDu erstellst bereits eine Blockcompression");
					}
				} else if (args[0].equalsIgnoreCase("spawn") && args.length == 2) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						CompressionArea area = Main.getInstance().getBAC(p.getUniqueId());
						if (!area.canBuild()) {
							p.sendMessage(
									Main.PREFIX + "Dein BAC ist eine Kopie. Bitte erstelle ein neues um es zu spawnen");
							return false;
						}

						EnumBuildType buildType = null;
						for (EnumBuildType type : EnumBuildType.values()) {
							if (args[1].toUpperCase().equals(type.name())) {
								buildType = type;
								break;
							}
						}

						if (buildType == null) {
							p.playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 1, 1);
							p.sendMessage(Main.PREFIX + "Du hast keine gültige Größe angeben!");
							return false;
						}
						p.sendMessage(Main.PREFIX + "§aDein BAC wird nun gebaut!");
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						area.build(p.getLocation(), buildType);
					} else {
						p.sendMessage(Main.PREFIX + "§cDu besitzt kein BAC.");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						p.sendMessage(Main.PREFIX + "§aDein BAC wird nun despawnt!");
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

						Main.getInstance().getBAC(p.getUniqueId()).deleteLatest();
					} else {
						p.sendMessage(Main.PREFIX + "§cDu besitzt kein BAC.");
					}
				} else if (args[0].equalsIgnoreCase("move")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())
							&& !Main.getInstance().getBAC(p.getUniqueId()).getArmorStands().isEmpty()) {
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

						if (!Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							Main.getInstance().getMoveHandler().startMoving(p);
							p.sendMessage(Main.PREFIX + "Du bist nun im den Baumodus");
						} else {
							Main.getInstance().getMoveHandler().stopMoving(p);
							p.sendMessage(Main.PREFIX + "Du hast den Baumodus verlassen!");
						}
					} else {
						p.sendMessage(Main.PREFIX + "§cDu besitzt kein BAC.");
					}
				} else if (args[0].equalsIgnoreCase("amount") && args.length == 2) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						if (Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							double d = Double.parseDouble(args[1]);

							Main.getInstance().getBAC(p.getUniqueId()).setMoveDuraion(d);
							p.sendMessage(Main.PREFIX + "Du hast die Geschwindikeit auf " + d + " gesetzt!");
						} else {
							p.sendMessage(Main.PREFIX + "§cDu befindest dich nicht im Move-Modus");
						}
					} else {
						p.sendMessage(Main.PREFIX + "§cDu besitzt kein BAC.");
					}
				} else if (args[0].equalsIgnoreCase("select")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						p.sendMessage(Main.PREFIX + "Dein BAC wurde zur Selektierung aus dem Cache entfernt");
					}

					int range = 100;
					if (args.length == 2) {
						range = Integer.parseInt(args[1]);
					}

					Main.getInstance().getSelectionHandler().addPlayer(p.getUniqueId(), range);
					p.sendMessage(Main.PREFIX + "Du bist nun im Selektierungsmodus!");
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
				} else if (args[0].equalsIgnoreCase("debug")) {
					p.getInventory().addItem(new ItemBuilder(Material.LOG, 1, (short) 15).setItem());
				}

			} else {
				p.sendMessage(Main.PREFIX + "§c Du hast keine Berechtigung für diesen Befehl!");
			}
		}
		return false;
	}

	private void sendInstructions(Player p) {
		p.sendMessage("§7------------------------------");
		p.sendMessage("§7/bac create | Erstelle ein neues BAC");
		p.sendMessage("§7/bac remove | Löscht dein zuletzt gespawntes BAC");
		p.sendMessage("§7/bac move | Toggelt den Move Modus");
		p.sendMessage("§7/bac select (<Range (Zahl)>) | Startet den selektierungs Modus");
		p.sendMessage("§7/bac amount <Zahl> | Setzt die Geschwindikeit");
		p.sendMessage("§7/bac spawn <big, medium, small> | Spawnt die compression");
	}

}
