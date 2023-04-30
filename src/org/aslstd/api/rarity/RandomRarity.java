package org.aslstd.api.rarity;

import java.util.List;

import org.aslstd.api.bukkit.value.Pair;
import org.aslstd.api.bukkit.value.util.NumUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomRarity {

	@NonNull
	private List<Pair<ERarity,Double>> list;

	public ERarity roll() {
		ERarity result = null;
		for (final Pair<ERarity,Double> entry : list) {
			if (NumUtil.isTrue(entry.getSecond()*100, 10000)) {
				result = entry.getFirst();
				break;
			}

		}

		return result;
	}

}
