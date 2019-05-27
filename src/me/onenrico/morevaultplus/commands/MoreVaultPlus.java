//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.onenrico.morevaultplus.converter.MyConverter;
import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.gui.MainMenu;
import me.onenrico.morevaultplus.listener.PlayerListener;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PermissionUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MoreVaultPlus implements CommandExecutor, Listener {
	String prefix;
	public static List<String> teks = new ArrayList<>();
	private static String aliases = "nonono";

	public MoreVaultPlus() {
		prefix = "";
	}

	public void setup(final Player p) {
		if (teks.isEmpty()) {
			teks = Locales.getValue("help_message");
		}
	}
//	public void setup(final Player p) {
//		MoreVaultPlus.teks.clear();
//		MoreVaultPlus.teks.add("<np>&e/mvp vault &7- &fOpen vault menu");
//		MoreVaultPlus.teks.add("<np>&e/mvp vault &7[&aID&7] &7- &fOpen your vault");
//		MoreVaultPlus.teks.add("<np>&b/mvp input &7[&aText&7] &7- &finput text from command");
//		if (PermissionUT.has(p, "morevaultplus.admin")) {
//			MoreVaultPlus.teks.add("<np>&b/mvp vault &7[&aPlayer&7] &7- &fOpen other vault");
//			MoreVaultPlus.teks.add("<np>&b/mvp vault &7[&aPlayer&7] &7[&aID&7] &7- &fOpen other vault");
//			MoreVaultPlus.teks.add("<np>&b/mvp give &7[&aPlayer&7] &7[&aamount&7] &7- &fGive vault to player");
//			MoreVaultPlus.teks.add("<np>&b/mvpet &7[&aPlayer&7] &7[&aamount&7] &7- &fSet owned vault of player");
//			MoreVaultPlus.teks.add("<np>&b/mvp convert &7[&aType&7] &7- &fConvert Database [Type: SQLite,MySQL,YML]");
//			MoreVaultPlus.teks
//					.add("<np>&b/mvp purify &7[&aPlayer&7] &7- &fpurify player vaults to reset its title and icon");
//			MoreVaultPlus.teks.add("<np>&b/mvp purifyall &7- &fpurify all player vaults to reset its title and icon");
//			MoreVaultPlus.teks.add("<np>&b/mvp reload &7- &freload the plugin");
//		}
//	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (MoreVaultPlus.aliases == null || MoreVaultPlus.aliases.equals("nonono")) {
			MoreVaultPlus.aliases = Core.configplugin.getStr("command-aliases", "pv");
		}
		String ce = e.getMessage().replace("/", "");
		if (ce.toLowerCase().startsWith(String.valueOf(MoreVaultPlus.aliases) + " ")) {
			ce = ce.replace(String.valueOf(MoreVaultPlus.aliases) + " ", "morevaultplus vault ");
			e.setMessage("/" + ce);
			return;
		}
		if (ce.equalsIgnoreCase(MoreVaultPlus.aliases)) {
			ce = ce.replace(MoreVaultPlus.aliases, "morevaultplus vault");
			e.setMessage("/" + ce);
		}
	}

	public void help(final Player p, final int page) {
		setup(p);
		Help.setup(MoreVaultPlus.teks, 5);
		Help.send(p, page);
	}

	public void help(final Player p, final String msg) {
		setup(p);
		Help.setup(MoreVaultPlus.teks, 5);
		Help.send(p, msg);
	}

	public Integer getId(final String arg) {
		Integer result = null;
		try {
			result = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			return null;
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public void handle(final Player p, final String[] args) {
		if (Datamanager.waiting.contains(p.getUniqueId())) {
			MessageUT.plmessage(p, Locales.getValue("please_wait"));
			return;
		}
		if (args.length > 0) {
			if (args.length == 3) {
				final PlaceholderUT pu = new PlaceholderUT();
				pu.add("target", args[1]);
				if (args[0].equalsIgnoreCase("give")) {
					if (!PermissionUT.check(p, "morevaultplus.vault.give")) {
						return;
					}
					final OfflinePlayer ofc = Bukkit.getOfflinePlayer(args[1]);
					if (ofc == null) {
						MessageUT.plmessage(p, Locales.getValue("no_target"), pu);
						return;
					}
					pu.add("target", ofc.getName());
					final int amount = MathUT.strInt(args[2]);
					Datamanager.addCurrentOwned(ofc.getUniqueId(), amount);
					pu.add("amount", new StringBuilder().append(amount).toString());
					pu.add("current",
							new StringBuilder().append(Datamanager.getCurrentOwned(ofc.getUniqueId())).toString());
					MessageUT.plmessage(p, Locales.getValue("vault_given"), pu);
					return;
				} else if (args[0].equalsIgnoreCase("set")) {
					if (!PermissionUT.check(p, "morevaultplus.vault.set")) {
						return;
					}
					final OfflinePlayer ofc = Bukkit.getOfflinePlayer(args[1]);
					if (ofc == null) {
						MessageUT.plmessage(p, Locales.getValue("no_target"), pu);
						return;
					}
					pu.add("target", ofc.getName());
					final int amount = MathUT.strInt(args[2]);
					Datamanager.setCurrentOwned(ofc.getUniqueId(), amount);
					pu.add("amount", new StringBuilder().append(amount).toString());
					pu.add("current", new StringBuilder().append(amount).toString());
					MessageUT.plmessage(p, Locales.getValue("vault_set"), pu);
					return;
				}
			}
			if (args[0].equalsIgnoreCase("convert")) {
				if (args.length == 2) {
					if (PermissionUT.check(p, "morevaultplus.convert")) {
						if (!args[1].equalsIgnoreCase("mysql") && !args[1].equalsIgnoreCase("sqlite")
								&& !args[1].equalsIgnoreCase("yml")) {
							this.help(p, "convert");
							return;
						}
						MyConverter.convert(p, args[1]);
					}
				} else {
					this.help(p, "convert");
				}
				return;
			}
			if (args[0].equalsIgnoreCase("help")) {
				if (args.length > 1) {
					final int page = getId(args[1]);
					if (page == -1) {
						this.help(p, args[1]);
					} else {
						this.help(p, (int) MathUT.clamp(page, 1L));
					}
					return;
				}
				this.help(p, 1);
				return;
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (args.length != 1) {
					this.help(p, "reload");
					return;
				}
				if (!PermissionUT.check(p, "morevaultplus.reload")) {
					return;
				}
				Core.getThis().reloadSetting();
				MessageUT.plmessage(p, Locales.getValue("config_reload"));
				return;
			} else if (args[0].equalsIgnoreCase("purifyall")) {
				if (!PermissionUT.check(p, "morevaultplus.purify")) {
					return;
				}
				for (final EVault ev : Datamanager.LoadedData) {
					ev.setIcon(EVault.defaulticon);
					ev.setTitle(EVault.defaulttitle);
					MainMenu.setup(p.getUniqueId());
					ev.setDescription(ItemUT.getLore(MainMenu.AvailableVault));
					ev.save();
				}
				final PlaceholderUT pu = new PlaceholderUT();
				pu.add("target", "All Players");
				MessageUT.plmessage(p, Locales.getValue("vault_purified"), pu);
				return;
			} else if (args[0].equalsIgnoreCase("purify")) {
				if (args.length != 2) {
					this.help(p, "purify");
					return;
				}
				if (!PermissionUT.check(p, "morevaultplus.purify")) {
					return;
				}
				final OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
				final PlaceholderUT pu2 = new PlaceholderUT();
				if (op == null) {
					pu2.add("target", args[1]);
					MessageUT.plmessage(p, Locales.getValue("no_target"), pu2);
				}
				pu2.add("target", op.getName());
				final UUID player = op.getUniqueId();
				MessageUT.plmessage(p, Locales.getValue("vault_purified"), pu2);
				for (final EVault ev2 : Datamanager.getOwnedVault(player)) {
					ev2.setIcon(EVault.defaulticon);
					ev2.setTitle(EVault.defaulttitle);
					ev2.setDescription(null);
					ev2.save();
				}
				return;
			} else {
				final CustomPermission cp = MoreVaultAPI.getCustomPermission(p, p.getWorld());
				if (args[0].equalsIgnoreCase("input")) {
					if (args.length < 2) {
						this.help(p, "input");
						return;
					}
					String msg = "";
					for (int i = 1; i < args.length; ++i) {
						msg = String.valueOf(msg) + args[i];
					}
					final EVault ev3 = PlayerListener.getCacheVault(p.getUniqueId());
					if (ev3 != null) {
						PlayerListener.process(p, Bukkit.getOfflinePlayer(ev3.getOwner()), ev3, msg);
					}
					return;
				} else if (args[0].equalsIgnoreCase("vault")) {
					if (args.length > 3) {
						this.help(p, "vault");
						return;
					}
					if (args.length == 1) {
						if (!PermissionUT.check(p, "morevaultplus.open")) {
							return;
						}
						MainMenu.open(p, p, 1);
						return;
					} else {
						Integer id = getId(args[1]);
						final boolean isPlayer = id == null;
						if (isPlayer) {
							if (!PermissionUT.check(p, "morevaultplus.open.other")) {
								return;
							}
							final OfflinePlayer op2 = Bukkit.getOfflinePlayer(args[1]);
							if (op2 == null) {
								final PlaceholderUT pu3 = new PlaceholderUT();
								pu3.add("target", args[1]);
								MessageUT.plmessage(p, Locales.getValue("no_target"), pu3);
							}
							if (args.length != 3) {
								MainMenu.open(p, op2, 1);
								return;
							}
							id = getId(args[2]);
							if (id == null) {
								this.help(p, "vault");
								return;
							}
							id = Math.abs(id);
							final PlaceholderUT pu3 = new PlaceholderUT();
							pu3.add("id", new StringBuilder().append(id).toString());
							if (id > MoreVaultAPI.getOwnedVault(op2, cp)) {
								MessageUT.plmessage(p, Locales.getValue("not_available"), pu3);
								return;
							}
							final EVault ev4 = Datamanager.getVaultByID(op2.getUniqueId(), id);
							ev4.openVault(null, p);
							ev4.setCloseaction(new Action() {
								@Override
								public void act() {
									ev4.save();
								}
							});
							return;
						} else {
							if (!PermissionUT.check(p, "morevaultplus.open")) {
								return;
							}
							id = Math.abs(id);
							final PlaceholderUT pu4 = new PlaceholderUT();
							pu4.add("id", new StringBuilder().append(id).toString());
							if (id > MoreVaultAPI.getOwnedVault(p, cp)) {
								MessageUT.plmessage(p, Locales.getValue("not_available"), pu4);
								return;
							}
							final EVault ev5 = Datamanager.getVaultByID(p.getUniqueId(), id);
							ev5.openVault(null, p);
							ev5.setCloseaction(new Action() {
								@Override
								public void act() {
									ev5.save();
								}
							});
							return;
						}
					}
				}
			}
		}
		this.help(p, 1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		Player p = null;
		if (cs instanceof Player) {
			p = (Player) cs;
			handle(p, args);
			return true;
		}
		if (args.length < 1) {
			MoreVaultPlus.teks.clear();
			MoreVaultPlus.teks.add("&b/mvp reload&7- &freload the plugin");
			MoreVaultPlus.teks.add("&b/mvp give &7[&aPlayer&7] &7[&aamount&7]&7- &fGive vault to player");
			MoreVaultPlus.teks.add("&b/mvp set &7[&aPlayer&7] &7[&aamount&7] &7- &fSet owned vault of player");
			MoreVaultPlus.teks
					.add("<np>&b/mvp purify &7[&aPlayer&7]&7- &fpurify player vaults to reset its title and icon");
			MoreVaultPlus.teks.add("<np>&b/mvp purifyall&7- &fpurify all player vaults to reset its title and icon");
			for (final String msg : MoreVaultPlus.teks) {
				MessageUT.cmsg(msg);
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			Core.getThis().reloadSetting();
			MessageUT.cmsg("Config Reloaded..!");
			return true;
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				final OfflinePlayer ofc = Bukkit.getOfflinePlayer(args[1]);
				if (ofc == null) {
					MessageUT.cmsg("Player not found");
					return true;
				}
				final int amount = MathUT.strInt(args[2]);
				Datamanager.addCurrentOwned(ofc.getUniqueId(), amount);
				MessageUT.cmsg("Success give " + amount + " vault to " + ofc.getName() + " currently have "
						+ Datamanager.getCurrentOwned(ofc.getUniqueId()) + " vaults");
			}
			if (args[0].equalsIgnoreCase("set")) {
				final OfflinePlayer ofc = Bukkit.getOfflinePlayer(args[1]);
				if (ofc == null) {
					MessageUT.cmsg("Player not found");
					return true;
				}
				final int amount = MathUT.strInt(args[2]);
				Datamanager.setCurrentOwned(ofc.getUniqueId(), amount);
				MessageUT.cmsg("Success set vault " + ofc.getName() + " amount to " + amount + " vaults");
			}
			if (args[0].equalsIgnoreCase("purifyall")) {
				for (final EVault ev : Datamanager.LoadedData) {
					ev.setIcon(EVault.defaulticon);
					ev.setTitle(EVault.defaulttitle);
					if (MainMenu.first) {
						MainMenu.setup(ev.getOwner());
					}
					ev.setDescription(ItemUT.getLore(MainMenu.AvailableVault));
					ev.save();
				}
				final PlaceholderUT pu = new PlaceholderUT();
				pu.add("target", "All Players");
				MessageUT.cmsg("All players Vault has Been Purified");
				return true;
			}
			if (args[0].equalsIgnoreCase("purify")) {
				final OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
				final PlaceholderUT pu2 = new PlaceholderUT();
				if (op == null) {
					pu2.add("target", args[1]);
					MessageUT.cmsg("Player " + args[1] + " Not Found");
				}
				pu2.add("target", op.getName());
				final UUID player = op.getUniqueId();
				MessageUT.cmsg(String.valueOf(op.getName()) + " Vault has Been Purified");
				for (final EVault ev2 : Datamanager.getOwnedVault(player)) {
					ev2.setIcon(EVault.defaulticon);
					ev2.setTitle(EVault.defaulttitle);
					ev2.setDescription(null);
					ev2.save();
				}
				return true;
			}
		}
		return true;
	}
}
