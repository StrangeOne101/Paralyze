package com.strangeone101.abilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ChiAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class ParalyzePlus extends ChiAbility implements AddonAbility 
{
	public static Map<Integer, Long> paralyzedTimes = new ConcurrentHashMap<Integer, Long>();
	public static Map<Integer, BukkitRunnable> paralyzedRunnables = new ConcurrentHashMap<Integer, BukkitRunnable>();
	
	//Records the current players shift state
	public static Map<Player, Boolean> isShifting = new ConcurrentHashMap<Player, Boolean>();
	
	public enum ParalyzeState {SNEAK, CLICK, BOTH};
	
	public static Map<Integer, ParalyzeState> paralyzed = new ConcurrentHashMap<Integer, ParalyzeState>();
	
	public static long duration;
	public static long cooldown;
	public static int maxHits;
	public static float hitRate;
	public static boolean slownessEnabled;
	public static int slownessLvl;
	public static long slownessDuration;
	
	public static final String moveName = "Paralyze";
	
	public static boolean is1_9 = false;
	
	public ParalyzePlus(Player player, final LivingEntity target, boolean isShift)
	{
		super(player);
		if (!bPlayer.canBend(this)) {
			return;
		}
		
		if (!ConfigManager.defaultConfig.get().getBoolean("Properties.Chi.CanBendWithWeapons") && GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType())) {
			return;
		}
		
		start();
		if(isRemoved()) {
			// stop if cancelled
			return;
		}
		
		if (paralyzed.containsKey(target.getEntityId())) {
			paralyzed.put(target.getEntityId(), ParalyzeState.BOTH);
			paralyzedRunnables.get(target.getEntityId()).cancel();
		} else {
			paralyzed.put(target.getEntityId(), isShift ? ParalyzeState.SNEAK : ParalyzeState.CLICK);
		}
		
		paralyzedTimes.put(target.getEntityId(), duration);
		if (isShift && target instanceof Player) isShifting.put((Player) target, ((Player) target).isSneaking());
		
		if (slownessEnabled) {
			if (target instanceof Player) {
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (slownessDuration / 50), slownessLvl));
			} else {
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (slownessDuration / 50), slownessLvl + (isShift ? 1 : 0)));
			}
		}
		
		spawnParticles(target.getLocation().clone().add(0, 1, 0), 50, isShift);
		
		for (int i = 0; i < 3; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (is1_9) {
						target.getWorld().playSound(target.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 1, 2);
					} else {
						target.getWorld().playSound(target.getLocation(), Sound.valueOf("CLICK"), 1, 2);
					}
				}
			}.runTaskLater(ProjectKorra.plugin, i * 2);
		}
		
		bPlayer.addCooldown(this);
		
		paralyzedRunnables.put(target.getEntityId(), new BukkitRunnable() {

			@Override
			public void run() {
				ParalyzeState state = paralyzed.get(target.getEntityId());
				paralyzedTimes.remove(target.getEntityId());
				paralyzedRunnables.remove(target.getEntityId());
				paralyzed.remove(target.getEntityId());
				
				if ((state == ParalyzeState.SNEAK || state == ParalyzeState.BOTH) && target instanceof Player && isShifting.containsKey((Player) target)) {
					Player p = (Player) target;
					p.setSneaking(isShifting.get(p));
					isShifting.remove(p);
				}
			}
			
		});
		paralyzedRunnables.get(target.getEntityId()).runTaskLater(ProjectKorra.plugin, duration / 50);
		
		remove();
	}
	
	@Override
	public long getCooldown() 
	{
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public String getName() {
		return moveName;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() 
	{
		remove();
	}

	@Override
	public String getAuthor() {
		return "StrangeOne101";
	}

	@Override
	public String getVersion() {
		return "1.3";
	}
	
	@Override
	public String getDescription() {
		return "Hit benders to paralyze them. If you hit them normally, they will be slowed and won't be "
				+ "able to click to bend. If you hit them while holding sneak, they won't be able to use "
				+ "sneak to bend for a while. This move has a long cooldown.";
	}

	@Override
	public void load() 
	{
		new ParalyzeListener();
		
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " v" + getVersion() + " loaded!");
		
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.Duration", 6500L);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.Cooldown", 9000L);
		//ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.MaxHits", 3);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Enabled", true);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Duration", 6500);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Level", 2);
		
		ConfigManager.defaultConfig.save();
		
		duration = ConfigManager.defaultConfig.get().getLong("ExtraAbilities.StrangeOne101.Paralyze.Duration");
		cooldown = ConfigManager.defaultConfig.get().getLong("ExtraAbilities.StrangeOne101.Paralyze.Cooldown");
		//maxHits = ConfigManager.defaultConfig.get().getInt("ExtraAbilities.StrangeOne101.Paralyze.MaxHits");
		slownessEnabled = ConfigManager.defaultConfig.get().getBoolean("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Enabled");
		slownessLvl = ConfigManager.defaultConfig.get().getInt("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Level") - 1;
		slownessDuration = ConfigManager.defaultConfig.get().getLong("ExtraAbilities.StrangeOne101.Paralyze.Slowness.Duration");
	
		Integer version = Integer.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].charAt(3));
		
		is1_9 = version >= 9;
	}

	@Override
	public void stop() {
		for (Player p : isShifting.keySet()) {
			p.setSneaking(isShifting.get(p));
		}
	}	
	
	public static void spawnParticles(Location block, int count, boolean isShift)
	{
		ParticleEffect.CRIT.display(block, count, 0.1, 0.1, 0.1, 1, 32);
		if (isShift) {
			ParticleEffect.CRIT_MAGIC.display(block, count / 2, 0.1, 0.1, 0.1, 1, 32);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
