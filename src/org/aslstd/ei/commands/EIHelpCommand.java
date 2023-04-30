package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.Usable;
import org.bukkit.command.CommandSender;

public class EIHelpCommand extends BasicCommand {

	public EIHelpCommand(BasicCommandHandler handler, String label, Usable<CommandSender, String[]> func) {
		super(handler, label, func);
	}

	@Override
	public String getDescription() {
		return "Shows all available commands";
	}

	@Override
	public String getUsage() {
		return "/eitems help";
	}

	@Override
	public String getPermission() {
		return "ei.commands.help";
	}

}
