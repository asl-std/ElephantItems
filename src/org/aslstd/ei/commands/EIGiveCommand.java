package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.SenderType;
import org.aslstd.api.bukkit.command.interfaze.Usable;
import org.bukkit.command.CommandSender;

public class EIGiveCommand extends BasicCommand {

	public EIGiveCommand(BasicCommandHandler handler, String label, Usable<CommandSender, String[]> func) {
		super(handler, label, func);
		senderType = SenderType.ALL;
	}

	@Override
	public String getDescription() {
		if (senderType == SenderType.CONSOLE_ONLY)
			return "Gives <eitem-id> to <player-name> player";
		else
			return "Gives <eitem-id> to you or to [player-name] player";
	}

	@Override
	public String getUsage() {
		if (senderType == SenderType.CONSOLE_ONLY)
			return "/eitems give <eitem-id> <player-name>";
		else
			return "/eitems give <eitem-id> [player-name]";
	}

	@Override
	public String getPermission() {
		return "ei.admin.give";
	}

}
