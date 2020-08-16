package com.strangeone101.abilities;

import java.util.HashMap;

import com.projectkorra.projectkorra.util.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.projectkorra.projectkorra.event.PlayerCooldownChangeEvent;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.util.WaterReturn;
import com.strangeone101.abilities.ParalyzePlus.ParalyzeState;

public class ParalyzeListener implements Listener
{
	private HashMap<Player, HashMap<String, BukkitRunnable>> blockedAbils = new HashMap();
	private HashMap<Player, Integer> bottles = new HashMap();
	
	public ParalyzeListener()
	{
		Bukkit.getPluginManager().registerEvents(this, ProjectKorra.plugin);
	}

	public String getActionbarMessage(ParalyzeState state) {
		return ChatColor.GOLD + "* Your " + (state == ParalyzeState.CLICK ? "arms are" : (state == ParalyzeState.SNEAK ? "legs are" : "body is")) + " paralyzed! *";
	}
	
	@EventHandler
	public void onHitEntity(EntityDamageByEntityEvent e)
	{
		if (e.isCancelled()) return;
		if (e.getDamager() instanceof Player) {
			if (!(e.getEntity() instanceof LivingEntity)) return;
				if (ParalyzePlus.paralyzed.containsKey(e.getDamager().getEntityId())) {
					ActionBar.sendActionBar(getActionbarMessage(ParalyzePlus.paralyzed.get(e.getDamager().getEntityId())), (Player) e.getDamager());
					e.setCancelled(true);
					return;
				}
			
			if (BendingPlayer.getBendingPlayer(((Player)e.getDamager())) != null)
			{
				BendingPlayer bp = BendingPlayer.getBendingPlayer(((Player)e.getDamager()));
				if (bp.canBend(CoreAbility.getAbility(ParalyzePlus.moveName))) {
					new ParalyzePlus(((Player)e.getDamager()), ((LivingEntity)e.getEntity()), ((Player)e.getDamager()).isSneaking());
				}
			}
		} else if (e.getDamager() instanceof LivingEntity) {
			if (ParalyzePlus.paralyzed.containsKey(e.getDamager().getEntityId())) {
				ParticleEffect.SMOKE_NORMAL.display(e.getDamager().getLocation().clone().add(0, 0.9, 0), 10, 0.4, 0.4, 0.4, 0.02, 80);
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerAnimationEvent e) {
		if (e.isCancelled()) return;
		if (ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.CLICK || state == ParalyzeState.BOTH) {
				ParticleEffect.SMOKE_NORMAL.display(e.getPlayer().getLocation().clone().add(0, 0.9, 0), 5, 0.4, 0.4, 0.F, 0.02F, 80);
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1F, 1.2F);
				ActionBar.sendActionBar(getActionbarMessage(ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId())), e.getPlayer());
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onCooldown(PlayerCooldownChangeEvent event) {
		String abil = event.getAbility();
		Player player = event.getPlayer();
		
		if(!blockedAbils.containsKey(player)) return;
		HashMap<String, BukkitRunnable> abils = blockedAbils.get(player);
		if(!abils.containsKey(abil)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onAbility(AbilityStartEvent event) {
		Ability abil = event.getAbility();
		Player player = abil.getPlayer();
		
		if(!blockedAbils.containsKey(player)) return;
		HashMap<String, BukkitRunnable> abils = blockedAbils.get(player);
		if(!abils.containsKey(abil.getName())) return;
		
		event.setCancelled(true);
	}
	
	private void fillBottle(Player player) {
		final PlayerInventory inventory = player.getInventory();
		final int index = inventory.first(Material.GLASS_BOTTLE);
		if (index >= 0) {
			final ItemStack item = inventory.getItem(index);

			final ItemStack water = WaterReturn.waterBottleItem();

			if (item.getAmount() == 1) {
				inventory.setItem(index, water);
			} else {
				item.setAmount(item.getAmount() - 1);
				inventory.setItem(index, item);
				final HashMap<Integer, ItemStack> leftover = inventory.addItem(water);
				for (final int left : leftover.keySet()) {
					player.getWorld().dropItemNaturally(player.getLocation(), leftover.get(left));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		if (e.getPlayer() != null && ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.CLICK || state == ParalyzeState.BOTH) {
				ActionBar.sendActionBar(getActionbarMessage(ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId())), e.getPlayer());
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(final PlayerInteractEvent e) {
		if (e.getPlayer() != null && ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.CLICK || state == ParalyzeState.BOTH) {
				ParticleEffect.SMOKE_NORMAL.display(e.getPlayer().getLocation().clone().add(0, 0.9, 0), 10, 0.4, 0.4, 0.4, 0.02F, 80);
				ActionBar.sendActionBar(getActionbarMessage(ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId())), e.getPlayer());
				e.setCancelled(true);
				
				final BendingPlayer bp = BendingPlayer.getBendingPlayer(e.getPlayer());
				if(bp == null) return;
				
				HashMap<String, BukkitRunnable> abils = new HashMap();
				if(blockedAbils.containsKey(e.getPlayer())) abils = blockedAbils.get(e.getPlayer());
				
				int firstBottle = WaterReturn.firstWaterBottle(e.getPlayer().getInventory());
				if(firstBottle > -1) bottles.put(e.getPlayer(), firstBottle);
				
				final String abil = bp.getBoundAbilityName();
				if(abils.containsKey(abil)) {
					//delay the runnable more.
					BukkitRunnable runnable = abils.get(abil);
					runnable.cancel();
					runnable.runTaskLater(ProjectKorra.plugin, 2L);
				} else {
					BukkitRunnable runnable = new BukkitRunnable() {

						@Override
						public void run() {
							blockedAbils.get(e.getPlayer()).remove(abil);
							
							if(bottles.containsKey(e.getPlayer())) {
								int firstBottle = WaterReturn.firstWaterBottle(e.getPlayer().getInventory());
								if(firstBottle != bottles.get(e.getPlayer())) {
									 fillBottle(e.getPlayer());
								}
							}

							bottles.remove(e.getPlayer());
						}
						
					};
					runnable.runTaskLater(ProjectKorra.plugin, 2L);
					abils.put(abil, runnable);
					blockedAbils.put(e.getPlayer(), abils);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.isCancelled()) return;
		if (ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.SNEAK || state == ParalyzeState.BOTH) {
				ParalyzePlus.isShifting.put(e.getPlayer(), e.isSneaking());
				ParticleEffect.SMOKE_NORMAL.display(e.getPlayer().getLocation().clone().add(0, 0.5, 0), 10, 0.4, 0.4, 0.4, 0.02, 80);
				ActionBar.sendActionBar(getActionbarMessage(ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId())), e.getPlayer());
				e.setCancelled(true);
			}
		}
	}
}
