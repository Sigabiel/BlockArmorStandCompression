package de.sigabiel.bac;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.sigabiel.bac.utils.EnumBuildType;
import de.sigabiel.bac.utils.EnumDirection;
import de.sigabiel.bac.utils.ItemBuilder;

// this class contains all the logic to build a block compression
public class CompressionArea {

	private World world;
	private int minX, maxX, minY, maxY, minZ, maxZ, index, taskID, globalID;
	private double moveDuraion = 0.2;
	private boolean move, canBuild;
	EnumBuildType buildType;
	private Location fixedPos;
	private ArrayList<ArmorStand> armorStands = new ArrayList<>();
	private ArrayList<Location> originalBlocks = new ArrayList<>();
	private ArrayList<Location> pasteBlocks = new ArrayList<>();

	public CompressionArea(Location first, Location second) {
		this.world = first.getWorld();

		minX = Math.min(first.getBlockX(), second.getBlockX());
		maxX = Math.max(first.getBlockX(), second.getBlockX());

		minY = Math.min(first.getBlockY(), second.getBlockY());
		maxY = Math.max(first.getBlockY(), second.getBlockY());

		minZ = Math.min(first.getBlockZ(), second.getBlockZ());
		maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					// preload every block that is copied later
					Location loc = new Location(world, x, y, z);
					originalBlocks.add(loc);
				}
			}
		}

		canBuild = true;
	}

	public CompressionArea(ArrayList<ArmorStand> stands) {
		this.armorStands = stands;

		// can build is an indicator for the system if the bac can be spawned or if it's
		// just moveable
		canBuild = false;
	}

	public void build(Location start, EnumBuildType buildType) {
		if (!canBuild) {
			return;
		}

		// fixed pos is the origin pos for the build
		fixedPos = new Location(world, minX, minY, minZ);
		this.buildType = buildType;
		pasteBlocks.clear();

		int difX = getDifference(minX, start.getBlockX());
		int difY = getDifference(minY, start.getBlockY());
		int difZ = getDifference(minZ, start.getBlockZ());

		for (int x = (minX + difX); x <= (maxX + difX); x++) {
			for (int y = (minY + difY); y <= (maxY + difY); y++) {
				for (int z = (minZ + difZ); z <= (maxZ + difZ); z++) {
					Location loc = new Location(world, x, y, z);
					pasteBlocks.add(loc);
				}
			}
		}

		index = 0;
		armorStands.clear();
		globalID = Main.getInstance().getGlobalIndex();
		Main.getInstance().addGlobalIndex();

		taskID = Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
			while (index >= (originalBlocks.size() - 1)
					|| originalBlocks.get(index).getBlock().getType() == Material.AIR) {
				index++;

				if (index >= (originalBlocks.size() - 1)) {
					Bukkit.getScheduler().cancelTask(taskID);
					return;
				}
			}

			Location targetLoc = pasteBlocks.get(index);
			buildArmorStand(targetLoc, buildType);
			index++;

		}, 0, 1);

	}

	private void buildArmorStand(Location targetLoc, EnumBuildType type) {
		float x = targetLoc.getBlockX()
				- (getDifference(fixedPos.getBlockX(), targetLoc.getBlockX()) * type.getMoveValue());

		float y = targetLoc.getBlockY()
				- (getDifference(fixedPos.getBlockY(), targetLoc.getBlockY()) * type.getMoveValue());

		float z = targetLoc.getBlockZ()
				- (getDifference(fixedPos.getBlockZ(), targetLoc.getBlockZ()) * type.getMoveValue());

		ArmorStand stand = targetLoc.getWorld().spawn(new Location(targetLoc.getWorld(), x, y, z), ArmorStand.class);
		stand.setCustomNameVisible(false);

		// name is given to indicate the compression later
		stand.setCustomName("bac " + globalID);

		stand.setGravity(false);
		stand.setSmall(type != EnumBuildType.BIG);
		stand.setVisible(false);
		ItemStack b = originalBlocks.get(index).getBlock().getState().getData().toItemStack();

		if (type == EnumBuildType.SMALL) {
			stand.setArms(true);
			stand.setItemInHand(originalBlocks.get(index).getBlock().getState().getData().toItemStack());
			stand.setRightArmPose(new EulerAngle(-0.714, 0.714, 12.248));
		} else {
			stand.setHelmet(new ItemBuilder(b.getType(), 1, (short) b.getDurability()).setItem());
		}

		armorStands.add(stand);
	}

	public ArrayList<ArmorStand> getArmorStands() {
		return armorStands;
	}

	public boolean canBuild() {
		return canBuild;
	}

	public void changeWholePosition(EnumDirection dir) {
		for (ArmorStand stand : armorStands) {
			changePosition(stand, dir);
		}
	}

	public void changePosition(ArmorStand stand, EnumDirection dir) {
		switch (dir) {
		case FORWARD:
			stand.teleport(stand.getLocation().add(0, 0, moveDuraion));
			break;
		case BACK:
			stand.teleport(stand.getLocation().add(0, 0, -moveDuraion));
			break;
		case LEFT:
			stand.teleport(stand.getLocation().add(moveDuraion, 0, 0));
			break;
		case RIGHT:
			stand.teleport(stand.getLocation().add(-moveDuraion, 0, 0));
			break;
		case UP:
			stand.teleport(stand.getLocation().add(0, moveDuraion, 0));
			break;
		case DOWN:
			stand.teleport(stand.getLocation().add(0, -moveDuraion, 0));
			break;
		default:
			break;
		}
	}

	public void changeRotation(ArmorStand stand, EnumDirection dir) {
		if (!stand.hasArms()) {
			switch (dir) {
			case RIGHT:
				stand.setHeadPose(stand.getHeadPose().add(0, -moveDuraion, 0));
				break;
			case LEFT:
				stand.setHeadPose(stand.getHeadPose().add(0, moveDuraion, 0));
				break;
			case UP:
				stand.setHeadPose(stand.getHeadPose().add(moveDuraion, 0, 0));
				break;
			case DOWN:
				stand.setHeadPose(stand.getHeadPose().add(-moveDuraion, 0, 0));
				break;
			case FORWARD:
				stand.setHeadPose(stand.getHeadPose().add(0, 0, moveDuraion));
				break;
			case BACK:
				stand.setHeadPose(stand.getHeadPose().add(0, 0, -moveDuraion));
			default:
				break;
			}
		} else {
			switch (dir) {
			case RIGHT:
				stand.setRightArmPose(stand.getRightArmPose().add(0, -moveDuraion, 0));
				break;
			case LEFT:
				stand.setRightArmPose(stand.getRightArmPose().add(0, moveDuraion, 0));
				break;
			case UP:
				stand.setRightArmPose(stand.getRightArmPose().add(moveDuraion, 0, 0));
				break;
			case DOWN:
				stand.setRightArmPose(stand.getRightArmPose().add(-moveDuraion, 0, 0));
				break;
			case BACK:
				stand.setRightArmPose(stand.getRightArmPose().add(0, 0, moveDuraion));
				break;
			case FORWARD:
				stand.setRightArmPose(stand.getRightArmPose().add(0, 0, -moveDuraion));
				break;
			default:
				break;
			}

		}

	}

	public void deleteLatest() {
		if (!armorStands.isEmpty()) {
			if (move) {
				Main.getInstance().getMoveHandler().stopMoving(Bukkit.getPlayer(Main.getInstance().getUUID(this)));
			}

			for (ArmorStand stand : armorStands) {
				stand.remove();
			}
		}

		armorStands.clear();
	}

	public void setMove(boolean move) {
		this.move = move;
	}

	public void setMoveDuraion(double moveDuraion) {
		this.moveDuraion = moveDuraion;
	}

	private static int getDifference(int originalPos, int posMultiply) {
		int result = Math.max(originalPos, posMultiply) - Math.min(originalPos, posMultiply);
		return (originalPos > posMultiply) ? -result : result;
	}
}
