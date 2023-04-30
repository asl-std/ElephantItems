package org.aslstd.ei.listener;

import org.aslstd.api.CustomParams;
import org.aslstd.api.bukkit.entity.EPlayer;
import org.aslstd.api.bukkit.equip.EquipSlot;
import org.aslstd.api.bukkit.events.equipment.EquipChangeEvent;
import org.aslstd.api.bukkit.items.IStatus;
import org.aslstd.api.bukkit.items.InventoryUtil;
import org.aslstd.api.bukkit.items.ItemStackUtil;
import org.aslstd.api.entity.RPGPlayer;
import org.aslstd.api.item.ItemType;
import org.aslstd.core.Core;
import org.aslstd.ei.EI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryChangeListener implements Listener {

	@EventHandler
	public void onHandsSwap(PlayerSwapHandItemsEvent e) {
		new BukkitRunnable() {

			@Override
			public void run() {
				final EPlayer p = EPlayer.getEPlayer(e.getPlayer());
				ItemStack hand = e.getPlayer().getInventory().getItemInMainHand(),
						offhand = e.getPlayer().getInventory().getItemInOffHand();

				ItemType thand = ItemType.ONE_HANDED,
						toff = ItemType.ONE_HANDED;

				if (EI.getGconfig().TYPE_RESTRICT) {

					ItemType temp = null;

					if (ItemStackUtil.validate(hand, IStatus.HAS_LORE)) {
						temp = ItemType.getByKey(CustomParams.TYPE.getValue(hand.getItemMeta().getLore().toString()));
						if (temp != null)
							thand = temp;
					}

					if (ItemStackUtil.validate(offhand, IStatus.HAS_LORE)) {
						temp = ItemType.getByKey(CustomParams.TYPE.getValue(offhand.getItemMeta().getLore().toString()));
						if (temp != null)
							toff = temp;
					}

					if (toff == ItemType.TWO_HANDED)
						if ((hand == null) || (hand.getType() == Material.AIR)) {
							hand = offhand.clone();
							offhand = null;

							e.getPlayer().getInventory().setItemInMainHand(hand);
							e.getPlayer().getInventory().setItemInOffHand(null);
						} else {
							e.getPlayer().getInventory().setItemInMainHand(offhand);
							e.getPlayer().getInventory().setItemInOffHand(null);
							InventoryUtil.addItem(hand.clone(), e.getPlayer());
							hand = offhand;
							offhand = null;
						}

				}

				if (hand != null) {
					if ((thand == ItemType.ONE_HANDED) || (thand == ItemType.TWO_HANDED) || (thand == ItemType.SHIELD)) {
						p.equip(EquipSlot.HAND, hand);
						RPGPlayer.calculateEquip(p, EquipSlot.HAND);
					}
				} else p.removeEquip(EquipSlot.HAND);

				if (offhand != null) {
					if ((thand == ItemType.ONE_HANDED) || (thand == ItemType.SHIELD)) {
						p.equip(EquipSlot.OFF, offhand);
						RPGPlayer.calculateEquip(p, EquipSlot.OFF);
					}
				} else p.removeEquip(EquipSlot.OFF);

				p.updateStats();
			}
		}.runTaskAsynchronously(Core.instance());
	}

	@EventHandler
	public void onEquipPrepare(EquipChangeEvent e) {
		final EPlayer p = EPlayer.getEPlayer(e.getPlayer());
		final ItemStack stack = e.getItemStack();

		new BukkitRunnable() {

			@Override
			public void run() {
				if ((e.getEquipSlot() == EquipSlot.HAND) || (e.getEquipSlot() == EquipSlot.OFF)) {
					ItemStack stack = e.getEquipSlot() == EquipSlot.HAND ? e.getPlayer().getInventory().getItemInMainHand() :
						e.getPlayer().getInventory().getItemInOffHand();

					ItemType type = ItemType.ONE_HANDED;
					EquipSlot eq = e.getEquipSlot();

					if (EI.getGconfig().TYPE_RESTRICT) {
						if (ItemStackUtil.validate(stack, IStatus.HAS_LORE))
							type = ItemType.getByKey(CustomParams.TYPE.getValue(stack.getItemMeta().getLore().toString()));

						if ((eq == EquipSlot.OFF) && (type == ItemType.TWO_HANDED)) {
							e.getPlayer().getInventory().setItemInOffHand(null);
							e.getPlayer().getInventory().setItemInMainHand(stack);
							eq = EquipSlot.HAND;
						}

						if (eq == EquipSlot.OFF) {
							final ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();

							if (ItemStackUtil.validate(hand, IStatus.HAS_LORE)) {
								final ItemType thand = ItemType.getByKey(CustomParams.TYPE.getValue(hand.getItemMeta().getLore().toString()));

								if ((thand == ItemType.TWO_HANDED) && (stack != null) && ((type == ItemType.ONE_HANDED) || (type == ItemType.SHIELD))) {
									InventoryUtil.addItem(stack, e.getPlayer());
									e.getPlayer().getInventory().setItemInOffHand(null);
									stack = null;
								}
							}
						}
					}

					if (stack != null) {
						if ((type == ItemType.ONE_HANDED) || ((eq == EquipSlot.HAND) && (type == ItemType.TWO_HANDED)) || (type == ItemType.SHIELD)) {
							p.equip(e.getEquipSlot(), stack);
							RPGPlayer.calculateEquip(p, eq);
						}
					} else p.removeEquip(e.getEquipSlot());

					return;
				}

				ItemType type = ItemType.ARMOR_HELMET;

				if (EI.getGconfig().TYPE_RESTRICT) {
					if (ItemStackUtil.validate(stack, IStatus.HAS_LORE))
						type = ItemType.getByKey(CustomParams.TYPE.getValue(stack.getItemMeta().getLore().toString()));

					if (!ItemType.isArmor(type)) {
						InventoryUtil.addItem(stack, e.getPlayer());

						e.getPlayer().getInventory().setItem(e.getEquipSlot().getSlotId(), null);

						p.removeEquip(e.getEquipSlot());
						return;
					}
				}

				if (stack != null) {
					p.equip(e.getEquipSlot(), stack);

					RPGPlayer.calculateEquip(p, e.getEquipSlot());
				} else p.removeEquip(e.getEquipSlot());

			}

		}.runTaskAsynchronously(Core.instance());
	}

	@EventHandler
	public void onPlayerDie(PlayerDeathEvent e) {
		if (e.getKeepInventory()) return;

		new BukkitRunnable() {

			@Override
			public void run() {
				if (e.getEntity().isDead()) return;

				cancel();
			}

		}.runTaskTimer(Core.instance(), 1L, 2L);
	}

}
