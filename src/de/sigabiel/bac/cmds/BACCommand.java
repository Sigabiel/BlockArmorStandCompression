package de.sigabiel.bac.cmds;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.sigabiel.bac.CompressionArea;
import de.sigabiel.bac.Main;
import de.sigabiel.bac.utils.EnumBuildType;

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
							p.sendMessage(Main.PREFIX + "Your latest BAC was deleted");
						}

						if (Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							Main.getInstance().getMoveHandler().stopMoving(p);
						}

						Main.getInstance().getIdleUsers().put(p.getUniqueId(), new Location[2]);
						p.sendMessage(Main.PREFIX + "Please hit two blocks to select the area of the BAC");

					} else {
						p.sendMessage(Main.PREFIX + "§cYou are already configuring a BAC");
					}
				} else if (args[0].equalsIgnoreCase("spawn") && args.length == 2) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						CompressionArea area = Main.getInstance().getBAC(p.getUniqueId());
						if (!area.canBuild()) {
							p.sendMessage(Main.PREFIX + "You can't spawn you BAC because it doesn't contain the original block area");
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
							p.sendMessage(Main.PREFIX + "The given size isn't valid!");
							return false;
						}

						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						area.build(p.getLocation(), buildType);
						p.sendMessage(Main.PREFIX + "§aYour BAC is buildung now!");

					} else {
						p.sendMessage(Main.PREFIX + "§cYou've no BAC!");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						p.sendMessage(Main.PREFIX + "§aYour BAC's despawning!");
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

						if (Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							Main.getInstance().getMoveHandler().stopMoving(p);
							p.sendMessage(Main.PREFIX + "You left the buildmode");
						}

						Main.getInstance().getBAC(p.getUniqueId()).deleteLatest();
					} else {
						p.sendMessage(Main.PREFIX + "§cYou've no BAC!");
					}
				} else if (args[0].equalsIgnoreCase("move")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())
							&& !Main.getInstance().getBAC(p.getUniqueId()).getArmorStands().isEmpty()) {
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

						if (!Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							Main.getInstance().getMoveHandler().startMoving(p);
							p.sendMessage(Main.PREFIX + "You joined the buildmode");
						} else {
							Main.getInstance().getMoveHandler().stopMoving(p);
							p.sendMessage(Main.PREFIX + "You left the buildmode");
						}
					} else {
						p.sendMessage(Main.PREFIX + "§cYou've no BAC!");
					}
				} else if (args[0].equalsIgnoreCase("amount") && args.length == 2) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						if (Main.getInstance().getMoveHandler().isMoving(p.getUniqueId())) {
							double d = Double.parseDouble(args[1]);

							Main.getInstance().getBAC(p.getUniqueId()).setMoveDuraion(d);
							p.sendMessage(Main.PREFIX + "You set the Speed to " + d);
						} else {
							p.sendMessage(Main.PREFIX + "§cYou're not in the buildmode");
						}
					} else {
						p.sendMessage(Main.PREFIX + "§cYou've no BAC!");
					}
				} else if (args[0].equalsIgnoreCase("select")) {
					if (Main.getInstance().hasBAC(p.getUniqueId())) {
						p.sendMessage(Main.PREFIX + "Your latest BAC was deleted");
					}

					int range = 100;
					if (args.length == 2) {
						range = Integer.parseInt(args[1]);
					}
					if (Main.getInstance().getSelectionHandler().isSelecting(p.getUniqueId())) {
						Main.getInstance().getSelectionHandler().removePlayer(p.getUniqueId());
						p.sendMessage(Main.PREFIX + "You've left the selection mode");
					} else {
						Main.getInstance().getSelectionHandler().addPlayer(p.getUniqueId(), range);
						p.sendMessage(Main.PREFIX + "You've joined the selection mode. Please interact with a BAC to select it.");
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
					}
				}

			} else {
				p.sendMessage(Main.PREFIX + "§cYou don't have the required permission to execute this command");
			}
		}
		return false;
	}

	private void sendInstructions(Player p) {
		p.sendMessage("§7------------------------------");
		p.sendMessage("§7/bac create | Creates a new BAC");
		p.sendMessage("§7/bac remove | Despawns your latest BAC");
		p.sendMessage("§7/bac move | Toggle build mode");
		p.sendMessage("§7/bac select (<Range (Zahl)>) | Toggle selection mode");
		p.sendMessage("§7/bac amount <Zahl> | Set the speed");
		p.sendMessage("§7/bac spawn <big, medium, small> | Spawns the BAC");
	}

}
