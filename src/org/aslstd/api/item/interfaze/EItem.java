package org.aslstd.api.item.interfaze;

import org.bukkit.inventory.ItemStack;
import org.dxrgd.api.bukkit.setting.impl.FileSettings;
import org.dxrgd.api.open.file.configuration.type.Yaml;

public interface EItem {

	ItemStack toStack();

	FileSettings getSettings();

	Yaml getFile();

	String getKey();

}
