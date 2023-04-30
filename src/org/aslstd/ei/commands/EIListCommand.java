package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.Usable;
import org.bukkit.command.CommandSender;

public class EIListCommand extends BasicCommand {

	public EIListCommand(BasicCommandHandler handler, String label, Usable<CommandSender, String[]> func) {
		super(handler, label, func);
	}

	@Override
	public String getDescription() {
		return "Shows list of eitems";
	}

	@Override
	public String getUsage() {
		return "/eitems list [page]";
	}

	@Override
	public String getPermission() {
		return "ei.commands.stats";
	}

}
