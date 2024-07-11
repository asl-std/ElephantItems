package org.aslstd.api.item.interfaze;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

public interface Serializable {

	Map<String, Object> serialize(ItemStack stack);

	ItemStack deserialize(Map<String, Object> data);

}