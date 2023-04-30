package org.aslstd.api.entity;

import java.util.List;

import org.aslstd.api.attributes.BasicAttr;
import org.aslstd.api.attributes.managers.WAttributes;
import org.aslstd.api.bukkit.entity.EPlayer;
import org.aslstd.api.bukkit.equip.EquipSlot;
import org.aslstd.api.bukkit.items.IStatus;
import org.aslstd.api.bukkit.items.ItemStackUtil;
import org.aslstd.api.bukkit.utils.BasicMetaAdapter;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RPGPlayer {

	public static double getAveragePercent(EPlayer ej, BasicAttr stat) {
		final double[] mult = { 0D, 0D };

		for (final EquipSlot slot : EquipSlot.values())
			if (ej.getTempSettings().hasRange("player.equip." + slot.name().toLowerCase() + "." + stat.getKey() + "-multiplier")) {
				final double[] val = ej.getTempSettings().getRange("player.equip." + slot.name().toLowerCase() + "." + stat.getKey() + "-multiplier");
				mult[0] = mult[0] + val[0];
				mult[1] = mult[1] + val[1];
			}

		if (mult[1] < mult[0]) return mult[0];
		return (mult[0] + mult[1]) / 2;
	}

	public static void calculateEquip(EPlayer ej, EquipSlot slot) {
		final ItemStack stack = ej.getEquipInventory().getEquip(slot);

		if (!ItemStackUtil.validate(stack, IStatus.HAS_LORE)) {
			ej.removeEquip(slot);
			return;
		}

		RPGPlayer.calculateStats(ej, stack, slot);
	}

	private static void calculateStats(EPlayer p, ItemStack stack, EquipSlot slot) {
		final ItemMeta meta = stack.getItemMeta();
		final List<String> lore = meta.getLore();

		for (final BasicAttr stat : WAttributes.getRegistered())
			if (BasicMetaAdapter.contains(lore, BasicAttr.getRegexPattern(stat))) {
				final String pref = BasicMetaAdapter.getStringValue(BasicAttr.getRegexPattern(stat), stack);

				final boolean isModifier = pref.endsWith("%");
				final boolean isNegative = pref.startsWith("-");

				final String[] value = pref.replaceFirst("[+-]*", "").replace("%", "").split("-");

				double first = 0D;
				double second = 0D;

				if ((value.length > 0) && NumUtil.isNumber(value[0])) {
					first = NumUtil.parseDouble(value[0]);
					second = NumUtil.parseDouble(value[0]);
				}

				if (value.length > 1)
					if (NumUtil.isNumber(value[1]))
						second = NumUtil.parseDouble(value[1]);

				if (isNegative) {
					first *= -1; second *= -1;
				}

				if ((first != 0D) && (second != 0D))
					if (!isModifier)
						p.getTempSettings().setRange("player.equip." + slot.name().toLowerCase() + "." + stat.getKey(), first, second);
					else
						p.getTempSettings().setRange("player.equip." + slot.name().toLowerCase() + "." + stat.getKey() + "-multiplier", first, second);
			}
	}

}
