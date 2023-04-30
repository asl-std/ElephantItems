package org.aslstd.api.item.interfaze;

import org.aslstd.api.bukkit.settings.StringSettings;
import org.aslstd.api.bukkit.yaml.YAML;
import org.bukkit.inventory.ItemStack;

public interface EItem {

	ItemStack toStack();

	StringSettings getSettings();

	YAML getFile();

	String getKey();

}
