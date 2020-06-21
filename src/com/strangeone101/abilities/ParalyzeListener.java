package com.strangeone101.abilities;

import org.bukkit.Bukkit;
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

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.strangeone101.abilities.ParalyzePlus.ParalyzeState;

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
		if (e.getDamager() instanceof Player) {
			if (!(e.getEntity() instanceof LivingEntity)) return;
				if (ParalyzePlus.paralyzed.containsKey(e.getDamager().getEntityId())) {
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
				Bukkit.broadcastMessage("state");
				ParticleEffect.SMOKE_NORMAL.display(e.getPlayer().getLocation().clone().add(0, 0.9, 0), 5, 0.4, 0.4, 0.F, 0.02F, 80);
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1F, 1.2F);
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		if (e.getPlayer() != null && ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.CLICK || state == ParalyzeState.BOTH) {
				e.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled()) return;

		if (e.getPlayer() != null && ParalyzePlus.paralyzed.containsKey(e.getPlayer().getEntityId())) {
			ParalyzeState state = ParalyzePlus.paralyzed.get(e.getPlayer().getEntityId());
			if (state == ParalyzeState.CLICK || state == ParalyzeState.BOTH) {
				ParticleEffect.SMOKE_NORMAL.display(e.getPlayer().getLocation().clone().add(0, 0.9, 0), 10, 0.4, 0.4, 0.4, 0.02F, 80);
				e.setCancelled(true);
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
				e.setCancelled(true);
			}
		}
	}
}
