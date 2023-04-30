package org.aslstd.ei.commands;

import java.util.ArrayList;
import java.util.List;

import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.ECommand;
import org.aslstd.api.bukkit.items.IStatus;
import org.aslstd.api.bukkit.items.InventoryUtil;
import org.aslstd.api.bukkit.items.ItemStackUtil;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.durability.material.RepairMaterial;
import org.aslstd.api.item.ESimpleItem;
import org.aslstd.ei.EI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EICommandHandler extends BasicCommandHandler {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		final List<String> result = new ArrayList<>();
		if (command.getName().equalsIgnoreCase(handler.getLabel())) {
			if (args.length == 1) {
				if (args[0].startsWith(help.getName()))
					if (help.testConditions(sender))
						result.add(getDefaultCommand().getName());

				for (final ECommand cmd : getRegisteredCommands())
					if (cmd.getName().startsWith(args[0]))
						if (cmd.testConditions(sender))
							result.add(cmd.getName());
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase(list.getName()))
					if (list.testConditions(sender))
						for (int i = 0 ; i < 1 ; i++)
							if (args[1].equalsIgnoreCase(String.valueOf(i)))
								result.add(String.valueOf(i));

				if (args[0].equalsIgnoreCase(hash.getName()))
					if (hash.testConditions(sender))
						for (final Player p : Bukkit.getOnlinePlayers())
							if (p.getName().startsWith(args[1]))
								result.add(p.getName());

				if (args[0].equalsIgnoreCase(give.getName()))
					if (give.testConditions(sender))
						for (final ESimpleItem item : ESimpleItem.getItems())
							if (item.getKey().startsWith(args[1]))
								result.add(item.getKey());
				if (args[0].equals(material.getName()))
					if (material.testConditions(sender))
						for (final RepairMaterial mat : RepairMaterial.getMaterials())
							if (mat.getKey().startsWith(args[1]))
								result.add(mat.getKey());
			}

			if (args.length == 3)
				if (args[0].equalsIgnoreCase(give.getName()))
					if (give.testConditions(sender))
						for (final Player p : Bukkit.getOnlinePlayers())
							if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()))
								result.add(p.getName());
		}

		return result;
	}

	private static EIHelpCommand	help;
	private static EIReloadCommand	reload;
	private static EIListCommand	list;
	private static EIGiveCommand	give;
	private static EIHashCommand	hash;
	private static EIMaterialCommand material;
	private static EICommandHandler	handler;

	private static ECommand getReloadCommand() {
		return reload == null ? reload = new EIReloadCommand(handler, "reload", (s, args) -> {
			EI.instance().reloadPlugin();
			return null;
		}) : reload;
	}

	private static ECommand getGiveCommand() {
		return give == null ? give = new EIGiveCommand(handler, "give", (s, args) -> {

			if (s instanceof CommandSender) {
				if (args.length <= 0)  return "&7You missed args: &e/eitems give &4<eitem-id> [player-name]";
			} else if (args.length <= 0) return "&7You missed args: &e/eitems give &4<eitem-id> &2[player-name]";

			if (s instanceof ConsoleCommandSender)
				if (args.length <= 1) return "&7You missed args: &e/eitems give <eitem-id> &4<player-name>";

			final ESimpleItem item = ESimpleItem.getById(args[0].toLowerCase());

			if (item == null)
				return "&4Item '" + args[0] + "' not found";

			Player player;
			if (args.length >= 2) player = EI.instance().getServer().getPlayerExact(args[1]);
			else player = (Player) s;

			if (player == null)  return "&4Player is not online!";

			InventoryUtil.addItem(item.toStack(), player);
			return null;

		}) : give;
	}

	private static ECommand getHelpCommand() {
		return help == null ? help = new EIHelpCommand(handler, "help", (s, args) -> {
			EText.send(s, "&c»------>&5[&6ElephantItems&5&l]");
			final List<ECommand> commands = new ArrayList<>(handler.getRegisteredCommands());
			commands.add(help);
			for (final ECommand command : commands)
				if (s.hasPermission(command.getPermission()))
					EText.send(s, "&6" + command.getUsage() + " - &2" + command.getDescription() + (s.isOp() || s.hasPermission("*") ? " &f- &5" + command.getPermission() : ""));
			EText.send(s, "&c»------>&5[&6ElephantItems&5&l]");
			return null;
		}) : help;
	}

	private static ECommand getListCommand() { // TODO Добавить JSON
		return list == null ? list = new EIListCommand(handler, "list", (s, args) -> {
			EText.send(s, "»------>[&6ElephantItems&5&l]");
			final List<String> items = new ArrayList<>(ESimpleItem.getRegisteredID());
			int v = 1;
			if (args.length >= 1 && NumUtil.isNumber(args[0])) v = NumUtil.parseInteger(args[0]);

			try {
				for (int i = v * 10 - 10; i != v * 10 && i < items.size(); i++)
					EText.send(s, "&6 • " + items.get(i));

			} catch (final ArrayIndexOutOfBoundsException e) {
				EText.send(s, "&c»------>&5[&6ElephantItems&5&l]");
				return null;
			}
			EText.send(s, "&c»------>&5[&6ElephantItems&5&l]");
			return null;
		}) : list;
	}

	private static ECommand getHashCommand() {
		return hash == null ? hash = new EIHashCommand(handler, "hash", (s, args) -> {
			if (args.length < 1) return "&7You missed args: &e/eitems hash &4<player> <itemhash>";

			if (args.length < 2) return "&7You missed args: &e/eitems hash <player> &4<itemhash>";

			final Player player = EI.instance().getServer().getPlayerExact(args[0]);

			if (player == null) return "&4Player is not online!";

			String pre = args[1];

			if (args.length > 2) for (int i = 2; i < args.length; i++)
				pre = pre + " " + args[i];

			final ItemStack stack = ItemStackUtil.toStack(pre);
			if (!ItemStackUtil.validate(stack, IStatus.HAS_MATERIAL)) return "&4Incorrect item hash! " + args[1];

			InventoryUtil.addItem(stack, player);
			return null;
		}) : hash;
	}

	public EICommandHandler() {
		super(EI.instance(), "eitems");
		handler = this;
		registerCommand(getHelpCommand());
		registerCommand(getGiveCommand());
		registerCommand(getListCommand());
		registerCommand(getHashCommand());
		registerCommand(getReloadCommand());
		registerCommand(new EIDebugCommand(this));
		registerCommand(material = new EIMaterialCommand(this));
	}

}
