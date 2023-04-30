package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.items.InventoryUtil;
import org.aslstd.api.durability.material.RepairMaterial;
import org.aslstd.ei.EI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class EIMaterialCommand extends BasicCommand {

	public EIMaterialCommand(BasicCommandHandler handler) {
		super(handler, "mat", (s,args) -> {
			if (s instanceof CommandSender) {
				if (args.length <= 0)
					return "&7You missed args: &e/eitems mat &4<id> <player-name>";
			} else if (args.length <= 0)
				return "&7You missed args: &e/eitems mat &4<id> &2[player-name]";

			if (s instanceof ConsoleCommandSender)
				if (args.length <= 1) return "&7You missed args: &e/eitems mat <id> &4<player-name>";

			final RepairMaterial mat = RepairMaterial.getMaterial(args[0]);

			if (mat == null) return "&4Repair material '" + args[0] + "' not found";

			Player player;
			if (args.length >= 2) player = EI.instance().getServer().getPlayerExact(args[1]);
			else player = (Player) s;

			if (player == null) return "&4Player " + args[1] + " is not online!";

			InventoryUtil.addItem(mat.toStack(), player);
			return null;
		});
	}

	@Override
	public String getDescription() {
		return "Gives repair material";
	}

	@Override
	public String getUsage() {
		return "/eitems mat <id> [player]";
	}

	@Override
	public String getPermission() {
		return "ei.commands.material";
	}

}
