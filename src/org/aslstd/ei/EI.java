package org.aslstd.ei;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aslstd.api.ability.EAbility;
import org.aslstd.api.ability.action.ActionManager;
import org.aslstd.api.attributes.managers.WAttributes;
import org.aslstd.api.bukkit.items.ItemStackUtil;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.yaml.YAML;
import org.aslstd.api.durability.material.RepairMaterial;
import org.aslstd.api.ejcore.plugin.EJPlugin;
import org.aslstd.api.item.ESimpleItem;
import org.aslstd.api.item.ItemManager;
import org.aslstd.api.rarity.RarityManager;
import org.aslstd.core.listeners.RegisterEventListener;
import org.aslstd.ei.commands.EICommandHandler;
import org.aslstd.ei.config.ConvertConfig;
import org.aslstd.ei.config.GConfig;
import org.aslstd.ei.config.LangConfig;
import org.aslstd.ei.listener.ConversionListener;
import org.aslstd.ei.listener.InventoryChangeListener;
import org.aslstd.ei.listener.RepairMaterialsListener;
import org.aslstd.ei.listener.ToolUsingListener;
import org.aslstd.ei.test.BasicTest;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class EI extends EJPlugin {

	public static final String prefix = "EIT";

	@Getter private static LangConfig		lang			= null;
	@Getter private static GConfig			gconfig			= null;
	@Getter private static ConvertConfig	convertConfig	= null;
	@Getter private static YAML 			guiConfig		= null;
	@Getter private static ActionManager	actionManager 	= null;
	private static EI						instance		= null;
	public static EI instance()	   { return instance; }

	public static List<String>	datas						= new ArrayList<>();
	public static List<String>	rangedWeapon				= new ArrayList<>();

	public static Map<Material,List<Integer>> predefined = new ConcurrentHashMap<>();

	public static boolean containsData(ItemStack stack) {
		return datas.contains(stack.getType().toString() + ":" + ItemStackUtil.getDamage(stack)) || datas.contains(stack.getType().toString());
	}

	public static boolean isRanged(Material mat) { return rangedWeapon.contains(mat.toString()); }

	@Override
	public int getPriority() { return 3; }

	@Override
	public void init() {
		instance = this;
		resourceId = 33799;
		lang = new LangConfig(getDataFolder() + "/lang.yml", this);
		gconfig = new GConfig(getDataFolder() + "/config.yml", this);
		guiConfig = new YAML(getDataFolder() + "/gui.yml", this);

		exportDefaults();
		final Metrics metrics = new Metrics(instance, 524);

		RegisterEventListener.register("inventoryCheckEvent", new InventoryChangeListener());
		RegisterEventListener.register("toolsUsing", new ToolUsingListener());
		RegisterEventListener.register("conversion", new ConversionListener());
		RegisterEventListener.register("repairMaterials", new RepairMaterialsListener());

		actionManager = new ActionManager();
		actionManager.register();

		EAbility.init();
		RepairMaterial.init();
		RarityManager.init();
		ItemManager.preInit();

		BasicTest.test();

		new EICommandHandler().registerHandler();

		ItemManager.process();
		convertConfig  = new ConvertConfig(getDataFolder() + "/conversion.yml", this);
		EText.fine("Currently " + RarityManager.rarities.size() + " rarities is registered", EI.prefix);
		EText.fine("Currently " + WAttributes.getRegistered().size() + " attributes is registered", EI.prefix);
		EText.fine("Currently " + ESimpleItem.getRegisteredID().size() + " items is registered", EI.prefix);

		metrics.addCustomChart(new SimplePie("itemsCreated", () -> {
			final int itemsCount = ESimpleItem.getRegisteredID().size();

			if (itemsCount > 1000 ) return ">1000";
			if (itemsCount > 750  ) return ">750";
			if (itemsCount > 500  ) return ">500";
			if (itemsCount > 250  ) return ">250";
			if (itemsCount > 100  ) return ">100";
			if (itemsCount > 50   ) return ">50";
			if (itemsCount > 25   ) return ">25";
			if (itemsCount > 10   ) return ">10";
			if (itemsCount > 1    ) return ">1";

			return "0/1";
		}));
	}

	@Override
	public void reloadPlugin() {
		WAttributes.reloadAttributes();
		RegisterEventListener.register("inventoryCheckEvent", new InventoryChangeListener());
		RegisterEventListener.register("toolsUsing", new ToolUsingListener());
		RegisterEventListener.register("conversion", new ConversionListener());
		RegisterEventListener.register("repairMaterials", new RepairMaterialsListener());

		ESimpleItem.unregisterAll();

		EI.getGconfig().reload();
		EI.getGuiConfig().reload();
		EI.getLang().reload();
		EI.getConvertConfig().reload();

		EI.instance().exportDefaults();

		ItemManager.preInit();

		actionManager = new ActionManager();
		actionManager.register();

		EAbility.init();
		RepairMaterial.init();
		RarityManager.init();
		ItemManager.preInit();

		ItemManager.process();

		EI.getConvertConfig().reload();

		EText.fine("Currently " + RarityManager.rarities.size() + " rarities is registered", EI.prefix);
		EText.fine("Currently " + EAbility.getRegistered().size() + " abilities is registered", EI.prefix);
		EText.fine("Currently " + WAttributes.getRegistered().size() + " attributes is registered", EI.prefix);
		EText.fine("Currently " + ESimpleItem.getRegisteredID().size() + " items is registered", EI.prefix);
		EText.fine("Currently " + RepairMaterial.getMaterials().size() + " repair materials is registered", EI.prefix);
		EText.fine("ElephantItems succesfully reloaded!", EI.prefix);
	}

	public final void exportDefaults() {
		final File itemsFolder = new File(getDataFolder() + "/items");
		final File rarityFolder = new File(getDataFolder() + "/rarity");
		final File abilityFolder = new File(getDataFolder() + "/abilities");

		YAML.exportFile("defaults/exampleItemTemplate.yml", this, getDataFolder());

		YAML.exportFile("defaults/allowedMaterials.txt", this, getDataFolder());

		if (!itemsFolder.exists()) {
			itemsFolder.mkdirs();

			YAML.exportFile("defaults/example.yml", this, itemsFolder);
		}

		if (!rarityFolder.exists()) {
			rarityFolder.mkdirs();

			YAML.exportFile("defaults/trash.yml", this, rarityFolder);
			YAML.exportFile("defaults/common.yml", this, rarityFolder);
			YAML.exportFile("defaults/uncommon.yml", this, rarityFolder);
			YAML.exportFile("defaults/rare.yml", this, rarityFolder);
			YAML.exportFile("defaults/epic.yml", this, rarityFolder);
			YAML.exportFile("defaults/mythical.yml", this, rarityFolder);
			YAML.exportFile("defaults/legendary.yml", this, rarityFolder);
		}

		if (!abilityFolder.exists()) {
			abilityFolder.mkdirs();

			YAML.exportFile("defaults/ability-mining.yml", this, abilityFolder);
			YAML.exportFile("defaults/ability-harvest.yml", this, abilityFolder);
		}
	}

}
