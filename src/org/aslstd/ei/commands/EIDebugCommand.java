package org.aslstd.ei.commands;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.SenderType;
import org.aslstd.api.bukkit.equip.EquipType;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.durability.DManager;
import org.bukkit.entity.Player;

public class EIDebugCommand extends BasicCommand {

	public EIDebugCommand(BasicCommandHandler handler) {
		super(handler, "debug", (s, args) -> {
			if (args.length <= 0)
				return "Incorrect argument: /eitems debug <debugType>";

			final Player p = (Player) s;

			switch (args[0].toLowerCase()) {
			case "durability":
				EText.send(p, DManager.getDurabilityString(p, EquipType.HAND));
				break;
			}
			return null;
		});
		senderType = SenderType.PLAYER_ONLY;
	}

}
