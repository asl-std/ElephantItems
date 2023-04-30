package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.SenderType;
import org.aslstd.api.bukkit.command.interfaze.Usable;
import org.bukkit.command.CommandSender;

public class EIHashCommand extends BasicCommand {

	public EIHashCommand(BasicCommandHandler handler, String label, Usable<CommandSender, String[]> func) {
		super(handler, label, func);
		senderType = SenderType.ALL;
	}

	@Override
	public String getDescription() {
		return "Gives <itemHash> to <player> player";
	}

	@Override
	public String getUsage() {
		return "/eitems hash  <player-name> <itemHash>";
	}

	@Override
	public String getPermission() {
		return "ei.admin.hash";
	}

}
