package com.strangeone101.abilities;

import org.bukkit.Bukkit;
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

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class ParalyzeListener implements Listener
{
	public ParalyzeListener()
	{
		Bukkit.getPluginManager().registerEvents(this, ProjectKorra.plugin);
	}
	
	@EventHandler
	public void onHitEntity(EntityDamageByEntityEvent e)
	{
		if (e.isCancelled()) return;
		if (!(e.getDamager() instanceof Player)) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		if (BendingPlayer.getBendingPlayer(((Player)e.getDamager())) != null)
		{
			BendingPlayer bp = BendingPlayer.getBendingPlayer(((Player)e.getDamager()));
			if (bp.canBend(CoreAbility.getAbility(ParalyzePlus.moveName))) {
				new ParalyzePlus(((Player)e.getDamager()), ((LivingEntity)e.getEntity()), ((Player)e.getDamager()).isSneaking());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerAnimationEvent e) {
		if (e.isCancelled()) return;
		if (!ParalyzePlus.isShifting.containsKey(e.getPlayer()) && ParalyzePlus.paralyzedTimes.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		if (e.getPlayer() != null && !ParalyzePlus.isShifting.containsKey(e.getPlayer()) && ParalyzePlus.paralyzedTimes.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled()) return;

		if (e.getPlayer() != null && !ParalyzePlus.isShifting.containsKey(e.getPlayer()) && ParalyzePlus.paralyzedTimes.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.isCancelled()) return;
		if (ParalyzePlus.isShifting.containsKey(e.getPlayer()) && ParalyzePlus.paralyzedTimes.containsKey(e.getPlayer())) {
			ParalyzePlus.isShifting.put(e.getPlayer(), e.isSneaking());
			e.setCancelled(true);
		}
	}
}
