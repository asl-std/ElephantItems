package org.aslstd.api.ability.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aslstd.api.ability.BasicAction;
import org.aslstd.api.bukkit.blocks.BlockUtil;
import org.aslstd.api.bukkit.equip.EquipSlot;
import org.aslstd.api.bukkit.items.InventoryUtil;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.settings.StringSettings;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.durability.DManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HarvestAction extends BasicAction {

	private int radius;
	private boolean eventPerBlock = false;
	private List<Material> harvestableCrops;

	public HarvestAction() {
		super("HARVEST");
		setFunc(e -> func(this, (PlayerInteractEvent) e));
	}

	@Override
	public void acceptSettings(StringSettings settings) {
		if (!settings.hasValue("settings.radius")) { EText.warn("Harvest action has incorrect settings"); return;}

		radius = NumUtil.parseInteger(settings.getValue("settings.radius"));

		if (settings.hasValue("settings.player-interact-event-per-block"))
			eventPerBlock = Boolean.parseBoolean("settings.player-interact-event-per-block");

		final List<String> crops = settings.exportArray("settings.crops");
		if (crops.isEmpty())
			harvestableCrops = new ArrayList<>(Arrays.asList(Material.WHEAT,Material.BEETROOTS,Material.POTATOES,Material.CARROTS,Material.NETHER_WART,Material.COCOA));
		else {
			harvestableCrops = new ArrayList<>();
			for (final String mat : crops) {
				final Material search = Material.matchMaterial(mat);
				if (search == null) {
					EText.debug("Incorrect material provided in crops list: " + mat + " in ability " + getKey() + ", material skipped");
				} else
					harvestableCrops.add(search);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void func(HarvestAction a, PlayerInteractEvent e) {
		if (e.isCancelled()) return;
		final List<Block> toHarvest = new ArrayList<>();
		if (a.radius <= 0)
			toHarvest.add(e.getClickedBlock());
		else toHarvest.addAll(BlockUtil.getBlocksSquare(e.getClickedBlock(), a.radius));

		toHarvest.removeIf(b -> !a.harvestableCrops.contains(b.getType()));

		if (toHarvest.isEmpty()) return;

		for (final Block b : toHarvest) {
			if (!(b.getBlockData() instanceof Ageable)) continue;
			final Ageable data = (Ageable) b.getBlockData();

			if (data.getAge() != data.getMaximumAge()) continue;

			final Collection<ItemStack> drops = b.getDrops();

			for (final ItemStack drop : drops)
				if (drop.getType().name().contains("SEED")) {
				}else
					InventoryUtil.addItem(drop, e.getPlayer());

			data.setAge(0);
			b.setBlockData(data);
			DManager.decreaseDurability(e.getPlayer(), EquipSlot.HAND, 1);
		}
	}

	@Override
	public boolean requiresThisEvent(Event e) {
		return e instanceof PlayerInteractEvent;
	}

	@Override
	public BasicAction clone() {
		return new HarvestAction();
	}
}
