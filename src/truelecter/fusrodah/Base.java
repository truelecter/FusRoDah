package truelecter.fusrodah;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Base extends JavaPlugin implements Listener {
	private HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	private long cooldown = 0;
	private HashMap<String, Long> cooldownsall = new HashMap<String, Long>();
	private long cooldownall = 0;
	private long itemid = 0;
	private boolean itemEnabled = false;
	private List<String> en;
	private Random rnd = new Random();
	private int range = 10;
	private String prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "FRD" + ChatColor.GRAY + "] "
			+ ChatColor.RESET;
	private String errorPrefix = prefix + ChatColor.RED;
	Date n = new Date();
	private long itemidall = 0;
	private List<String> notified = new ArrayList<String>();
	private List<String> notifiedAll = new ArrayList<String>();
	private List<String> toNotify = new ArrayList<String>();
	private List<String> toNotifyAll = new ArrayList<String>();

	private void effectPlayer(Player p) {
		p.getWorld().playEffect(p.getLocation(), Effect.BLAZE_SHOOT, 0, 20);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		cooldowns.put(e.getPlayer().getName(), 0L);
		cooldownsall.put(e.getPlayer().getName(), 0L);
		if (toNotify.contains(e.getPlayer().getName()) && toNotifyAll.contains(e.getPlayer().getName())) {
			e.getPlayer().sendMessage(prefix + "Кулдаун обеих отталкиваний прошел.");
		}
		if (toNotify.contains(e.getPlayer().getName())) {
			e.getPlayer().sendMessage(prefix + "Кулдаун отталкивания прошел.");
		}
		if (toNotifyAll.contains(e.getPlayer().getName())) {
			e.getPlayer().sendMessage(prefix + "Кулдаун отталкивания всех существ прошел.");
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!itemEnabled) {
			return;
		}
		if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(
				Action.RIGHT_CLICK_BLOCK))) {
			return;
		}
		if (event.getItem() == null || event.getItem().getTypeId() != itemid && event.getItem().getTypeId() != itemidall) {
			return;
		}
		if (!en.contains(event.getPlayer().getName())) {
			return;
		}
		Player p = event.getPlayer();
		if (event.getItem().getTypeId() == itemid) {
			if (!event.getPlayer().hasPermission("frd.item")) {
				return;
			}
			if (event.getPlayer().hasPermission("frd.cooldown.bypass")) {
				if (punch(event.getPlayer())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
					effectPlayer(p);
				}
				cooldowns.put(event.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));
				notified.remove(event.getPlayer().getName());
				return;
			}
			if (!(getCD(event.getPlayer()) > 0L)) {
				if (punch(event.getPlayer())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
					effectPlayer(p);
				}
				cooldowns.put(event.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));
				notified.remove(event.getPlayer().getName());
				return;
			} else {
				event.getPlayer().sendMessage(errorPrefix + "Кулдаун: " + getCD(event.getPlayer()));
				return;
			}
		}
		if (event.getItem().getTypeId() == itemidall) {
			if (!event.getPlayer().hasPermission("frd.item.all")) {
				return;
			}
			if (event.getPlayer().hasPermission("frd.cooldown.all.bypass")) {
				if (punchAll(event.getPlayer())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
					effectPlayer(p);
				}
				cooldownsall.put(event.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));
				notifiedAll.remove(event.getPlayer().getName());
				return;
			}
			if (!(getCDAll(event.getPlayer()) > 0L)) {
				if (punchAll(event.getPlayer())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
					effectPlayer(p);
				}
				cooldownsall.put(event.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));
				notifiedAll.remove(event.getPlayer().getName());
				return;
			} else {
				event.getPlayer().sendMessage(errorPrefix + "Кулдаун: " + getCDAll(event.getPlayer()));
				return;
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getLabel().equalsIgnoreCase("frdc")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(errorPrefix + "Эта комманда может быть выполнена только игроком");
				return true;
			}

			Player p = (Player) sender;
			if (args.length == 0) {
				if (p.hasPermission("frd.cmd")) {
					if (en.contains(p.getName())) {
						en.remove(p.getName());
						sender.sendMessage(prefix + "Отключено!");
					} else {
						en.add(p.getName());
						sender.sendMessage(prefix + "Включено!");
					}
					return true;
				} else {
					sender.sendMessage(errorPrefix + "Нет прав.");
					return true;
				}
			}

		}
		if (cmd.getLabel().equalsIgnoreCase("frdp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(errorPrefix + "Эта комманда может быть выполнена только игроком");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 0) {
				if (p.hasPermission("frd.cmd.punch")) {
					if (!(getCD(p) > 0L)) {
						if (punch(p)) {
							sender.sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
						}
						cooldowns.put(sender.getName(), Long.valueOf(System.currentTimeMillis()));
					} else {
						sender.sendMessage(errorPrefix + "Кулдаун: " + getCD(p));
						notified.remove(p.getName());
					}
					return true;
				} else {
					sender.sendMessage(errorPrefix + "Нет прав.");
					return true;
				}
			}

		}
		if (cmd.getLabel().equalsIgnoreCase("frda")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(errorPrefix + "Эта комманда может быть выполнена только игроком");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 0) {
				if (p.hasPermission("frd.cmd.punch.all")) {
					if (!(getCDAll(p) > 0L)) {
						if (punchAll(p)) {
							sender.sendMessage(ChatColor.RED + "Fus! Ro! DAH!!!");
						}
						cooldownsall.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
					} else {
						sender.sendMessage(errorPrefix + "Кулдаун: " + getCDAll(p));
						notifiedAll.remove(p.getName());
					}
					return true;
				} else {
					sender.sendMessage(errorPrefix + "Нет прав.");
					return true;
				}
			}

		}
		return false;
	}

	private long getCD(Player p) {
		if (cooldowns.containsKey(p.getName())) {
			long secondsLeft = ((Long) cooldowns.get(p.getName())).longValue() / 1000L + cooldown
					- System.currentTimeMillis() / 1000L;
			return secondsLeft;
		}
		cooldowns.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
		long secondsLeft = 0L;
		return secondsLeft;
	}

	private long getCDAll(Player p) {
		if (cooldownsall.containsKey(p.getName())) {
			long secondsLeft = ((Long) cooldownsall.get(p.getName())).longValue() / 1000L + cooldownall
					- System.currentTimeMillis() / 1000L;
			return secondsLeft;
		}
		cooldownsall.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
		long secondsLeft = 0L;
		return secondsLeft;
	}

	private long getCD(String p) {
		if (cooldowns.containsKey(p)) {
			long secondsLeft = ((Long) cooldowns.get(p)).longValue() / 1000L + cooldown
					- System.currentTimeMillis() / 1000L;
			return secondsLeft;
		}
		cooldowns.put(p, Long.valueOf(System.currentTimeMillis()));
		long secondsLeft = 0L;
		return secondsLeft;
	}

	private long getCDAll(String p) {
		if (cooldownsall.containsKey(p)) {
			long secondsLeft = ((Long) cooldownsall.get(p)).longValue() / 1000L + cooldownall
					- System.currentTimeMillis() / 1000L;
			return secondsLeft;
		}
		cooldownsall.put(p, Long.valueOf(System.currentTimeMillis()));
		long secondsLeft = 0L;
		return secondsLeft;
	}

	private boolean punch(Player player) {
		Vector v = player.getEyeLocation().getDirection().multiply(rnd.nextDouble() * 10);
		Entity e = getTarget(player);
		if (e == null) {
			player.sendMessage(errorPrefix + "Заряд потратился впустую.");
			cooldowns.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
			return false;
		} else {
			punch(e, v);
			cooldowns.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
			return true;
		}
	}

	private boolean punchAll(Player player) {
		List<Entity> e = player.getNearbyEntities(range, range, range);
		Vector v;
		if (e.isEmpty()) {
			player.sendMessage(errorPrefix + "Заряд потратился впустую.");
			cooldownsall.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
			return false;
		} else {
			for (Entity l : e) {
				v = player.getEyeLocation().getDirection().multiply(rnd.nextInt(10));
				punch(l, v);
			}
			cooldownsall.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
			return true;
		}
	}

	private void punch(Entity e, Vector v) {
		e.setVelocity(v);
	}

	private Entity getTarget(Player player) {
		List<Entity> nearbyE = player.getNearbyEntities(range, range, range);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();
		for (Entity e : nearbyE) {
			if (e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}
		Entity target = null;
		BlockIterator bItr = new BlockIterator(player, range);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			for (LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75)
						&& (by - 1 <= ey && ey <= by + 2.5)) {
					target = e;
					break;
				}
			}
		}
		return target;
	}

	public void onEnable() {
		if (getConfig().getLong("cooldown", -1) < 0) {
			getConfig().set("cooldown", 30);
		}
		if (getConfig().getLong("cooldownall", -1) < 0) {
			getConfig().set("cooldownall", 60);
		}
		if (getConfig().getLong("item.id", -1) < 0) {
			getConfig().set("item.id", 280);
		}
		if (!getConfig().isBoolean("item.enabled")) {
			getConfig().set("item.enabled", false);
		}
		if (getConfig().getLong("item.idall", -1) < 0) {
			getConfig().set("item.idall", 369);
		}
		if (!getConfig().isList("players.list")) {
			en = new ArrayList<String>();
		} else {
			en = getConfig().getStringList("players.list");
		}
		if (!getConfig().isList("players.notified")) {
			notified = new ArrayList<String>();
		} else {
			notified = getConfig().getStringList("players.notified");
		}
		if (!getConfig().isList("players.notifiedAll")) {
			notifiedAll = new ArrayList<String>();
		} else {
			notifiedAll = getConfig().getStringList("players.notifiedAll");
		}
		if (!getConfig().isList("players.toNotify")) {
			toNotify = new ArrayList<String>();
		} else {
			toNotify = getConfig().getStringList("players.toNotify");
		}
		if (!getConfig().isList("players.toNotifyAll")) {
			toNotifyAll = new ArrayList<String>();
		} else {
			toNotifyAll = getConfig().getStringList("players.toNotifyAll");
		}
		if (getConfig().getInt("range", -1) < 0) {
			getConfig().set("range", 10);
		}
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
		cooldown = getConfig().getLong("cooldown", 30);
		cooldownall = getConfig().getLong("cooldownall", 60);
		itemid = getConfig().getLong("item.id", 280);
		itemEnabled = getConfig().getBoolean("item.enabled", false);
		itemidall = getConfig().getLong("item.idall", 369);
		range = getConfig().getInt("range", 10);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (String n : en) {
					Player p = getServer().getPlayer(n);
					if (getCD(n) < 0L) {
						if (p == null) {
							toNotify.add(n);
							continue;
						}
						if (!p.hasPermission("frd.cooldown.bypass"))
						if (!notified.contains(n)) {
							p.sendMessage(prefix + "Кулдаун отталкивания прошел.");
							notified.add(n);
						}
					}
					if (getCDAll(n) < 0L) {
						if (p == null) {
							toNotifyAll.add(n);
							continue;
						}
						if (!p.hasPermission("frd.cooldown.all.bypass"))
						if (!notifiedAll.contains(n)) {
							p.sendMessage(prefix + "Кулдаун отталкивания ближайших существ прошел.");
							notifiedAll.add(n);
						}
					}

				}
			}
		}, 0L, 20L);
	}

	public void onDisable() {
		getConfig().set("players.list", en);
		getConfig().set("players.notified", notified);
		getConfig().set("players.notifiedAll", notifiedAll);
		getConfig().set("players.toNotify", toNotify);
		getConfig().set("players.toNotifyAll", toNotifyAll);
		saveConfig();
	}
}
