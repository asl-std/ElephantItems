package org.aslstd.api;

import org.aslstd.api.bukkit.value.CustomParam;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.item.ItemType;
import org.aslstd.api.rarity.RarityManager;
import org.aslstd.ei.EI;

public class CustomParams {

	public static final CustomParam LEVEL 	= new CustomParam("level", EI.getLang()) {

		@Override
		protected boolean isAllowedValue(String value) {
			if (NumUtil.isNumber(value))
				return true;
			else throw new IllegalArgumentException("You must set the integer value for level!");
		}

	};

	public static final CustomParam RARITY 	= new CustomParam("rarity", EI.getLang()) {

		@Override
		protected boolean isAllowedValue(String value) {
			if (RarityManager.getById(value) != null)
				return true;
			else throw new IllegalArgumentException(
					"You must use one of the key listed in your plugins/ElephantItems/rarity folder, you can't set non-existed value. "
							+ "If u want to decorate Item with Rarity, set it in description");
		}

	};

	public static final CustomParam TYPE 	= new CustomParam("type", EI.getLang()) {

		@Override
		protected boolean isAllowedValue(String value) {
			if (ItemType.getByKey(value) != null)
				return true;
			else throw new IllegalArgumentException("You must use one of parameters listed in ItemType! You try to use [ Type:" + value + " ]");
		}

	};

	public static final CustomParam SOCKET = new CustomParam("socket", EI.getLang()) {

		@Override
		protected boolean isAllowedValue(String value) {

			return false;
		}

	};

}
