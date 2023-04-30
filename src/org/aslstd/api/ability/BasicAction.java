package org.aslstd.api.ability;

import org.aslstd.api.bukkit.settings.StringSettings;
import org.bukkit.event.Event;
import org.bukkit.util.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class BasicAction {

	@Getter private String key;

	private Consumer<Object> func;

	public BasicAction(String key) { this.key = key; }

	protected void setFunc(Consumer<Object> func) {
		if (this.func == null)
			this.func = func;
	}

	public void accept(Event e) { if (func != null) func.accept(e); }

	public abstract boolean requiresThisEvent(Event e);

	public abstract void acceptSettings(StringSettings settings);

	@Override
	public abstract BasicAction clone();

}
