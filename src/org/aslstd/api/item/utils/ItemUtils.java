package org.aslstd.api.item.utils;

import java.util.ArrayList;
import java.util.List;

import org.aslstd.api.CustomParams;
import org.aslstd.api.attributes.AttrBase;
import org.aslstd.api.attributes.AttrManager;
import org.aslstd.api.item.ESimpleItem;
import org.aslstd.api.rarity.ERarity;
import org.aslstd.api.rarity.RarityManager;
import org.aslstd.ei.EI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dxrgd.api.bukkit.utility.BasicMetaAdapter;
import org.dxrgd.api.bukkit.utility.IStatus;
import org.dxrgd.api.bukkit.utility.ItemStackUtil;
import org.dxrgd.api.open.value.Pair;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemUtils {

	public static ItemStack reroll(ItemStack source) {
		if (!ItemStackUtil.validate(source, IStatus.HAS_LORE)) return null;

		final NBTItem item = new NBTItem(source);

		if (!item.hasTag("ei-id")) return null;

		final ESimpleItem eItem = ESimpleItem.getById(item.getString("ei-id"));

		return eItem.toStack();
	}

	public static Pair<AttrBase,String> upgradeStat(Pair<AttrBase,String> attribute, ERarity rarity) {
		attribute.setSecond(attribute.getSecond().replace('1', '3'));
		return attribute;
	}

	public static ItemStack upgrade(ItemStack source) {
		if (!ItemStackUtil.validate(source, IStatus.HAS_LORE)) return null;
		if (!isUpgradable(source)) return null;
		final NBTItem item = new NBTItem(source);
		final ERarity rarity = RarityManager.getById(item.getString("ei-rarity"));
		if (rarity == null) return null;
		final int level = item.getInteger("ei-level");
		final List<Pair<AttrBase,String>> collectedStats = new ArrayList<>();

		final ItemStack target = source.clone();
		final ItemMeta targetMeta = target.getItemMeta();

		final List<String> lore = source.getItemMeta().getLore();

		final int lvIndex = BasicMetaAdapter.indexOf(lore, CustomParams.LEVEL.getPattern());

		if (lvIndex == -1) return null;

		for (final AttrBase attr : AttrManager.getAttributes()) {
			final String val = BasicMetaAdapter.getStringValue(AttrBase.getRegexPattern(attr), lore);

			if (!val.equalsIgnoreCase("")) collectedStats.add(new Pair<>(attr,val));
		}

		collectedStats.forEach(pair -> upgradeStat(pair, rarity));

		for (final Pair<AttrBase,String> attr : collectedStats) {
			final int index = BasicMetaAdapter.indexOf(lore, AttrBase.getRegexPattern(attr.getFirst()));

			if (index != -1)
				lore.set(index, attr.getFirst().convertToLore(attr.getSecond()));
		}

		lore.set(lvIndex, CustomParams.LEVEL.convert(String.valueOf(level + 1)));

		targetMeta.setLore(lore);
		target.setItemMeta(targetMeta);

		return target;
	}

	public static boolean isUpgradable(ItemStack source) {
		final NBTItem item = new NBTItem(source);

		if (!item.hasTag("ei-rarity")) return false;
		if (!item.hasTag("ei-level")) return false;
		if (!item.hasTag("ei-upgrades")) return true;

		return item.getInteger("ei-upgrades") < EI.getGconfig().MAX_ITEM_UPGRADES;
	}

}
