package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.SenderType;
import org.aslstd.api.bukkit.command.interfaze.Usable;
import org.bukkit.command.CommandSender;

public class EIReloadCommand extends BasicCommand {

	public EIReloadCommand(BasicCommandHandler handler, String label, Usable<CommandSender, String[]> func) {
		super(handler, label, func);
		senderType = SenderType.ALL;
	}

	@Override
	public String getDescription() {
		return "Reloads all config files and items.";
	}

	@Override
	public String getUsage() {
		return "/eitems reload";
	}

	@Override
	public String getPermission() {
		return "ei.admin.reload";
	}

}
