//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.object;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.gui.MainMenu;
import me.onenrico.morevaultplus.hook.WorldGuardHook;
import me.onenrico.morevaultplus.listener.PlayerListener;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.menu.MenuAnimation;
import me.onenrico.morevaultplus.menu.VaultMenu;
import me.onenrico.morevaultplus.sorter.Sorting;
import me.onenrico.morevaultplus.utils.ArrayUT;
import me.onenrico.morevaultplus.utils.EMaterial;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PermissionUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;
import me.onenrico.morevaultplus.utils.PlayerUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class EVault extends VaultMenu {
	public static int defaultspace;
	public static String defaulttitle;
	public static String defaulticon;
	private String identifier;
	@SuppressWarnings("unused")
	private int space;
	private String icon;
	private List<String> description;
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack PrevPageMaxItem;
	private static ItemStack NextPageMaxItem;
	private static ItemStack BalanceItem;
	private static ItemStack ExpItem;
	private static ItemStack BorderItem;
	private static ItemStack SortItem;
	public static Boolean first;
	private double balance;
	private double exp;

	static {
		EVault.first = true;
	}

	public void setup() {
		if (EVault.first) {
			EVault.PrevPageItem = GUIMenu.setupItem(this, "PrevPageItem");
			EVault.NextPageItem = GUIMenu.setupItem(this, "NextPageItem");
			EVault.PrevPageMaxItem = GUIMenu.setupItem(this, "PrevPageMaxItem");
			EVault.NextPageMaxItem = GUIMenu.setupItem(this, "NextPageMaxItem");
			EVault.BalanceItem = GUIMenu.setupItem(this, "BalanceItem");
			EVault.ExpItem = GUIMenu.setupItem(this, "ExpItem");
			EVault.BorderItem = GUIMenu.setupItem(this, "BorderItem");
			EVault.SortItem = GUIMenu.setupItem(this, "SortItem");
			EVault.first = false;
		}
	}

	public void dropInv(final Player p, final int slot) {
		if (inv != null && inv.getItem(slot) != null && !inv.getItem(slot).getType().equals(Material.AIR)) {
			if (GUIMenu.isSecured(inv.getItem(slot))) {
				return;
			}
			p.getWorld().dropItem(p.getLocation(), inv.getItem(slot));
		}
	}

	public static boolean getBlocks(final Block start, final int radius) {
		if (!check(start.getType())) {
			return false;
		}
		for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; ++x) {
			for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; ++y) {
				for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; ++z) {
					final Location loc = new Location(start.getWorld(), x, y, z);
					if (!check(loc.getBlock().getType())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean check(final Material m) {
		String pref = "";
		if (ReflectionUT.NUMBER_VERSION >= 113) {
			pref = "LEGACY_";
		}
		return !m.equals(Material.getMaterial(String.valueOf(pref) + "WATER_LILY"))
				&& !m.equals(Material.getMaterial(String.valueOf(pref) + "CARPET"))
				&& !m.equals(Material.getMaterial(String.valueOf(pref) + "TRAP_DOOR"))
				&& !m.equals(Material.getMaterial(String.valueOf(pref) + "LADDER"))
				&& !m.equals(Material.getMaterial(String.valueOf(pref) + "IRON_TRAPDOOR"));
	}

	public void openVault(PlaceholderUT pu, final Player p) {
		if (ConfigManager.worldperm && !PermissionUT.check(p, "morevaultplus.open." + p.getWorld().getName())) {
			return;
		}
		if (Datamanager.waiting.contains(getOwner())) {
			MessageUT.plmessage(p, Locales.getValue("please_wait"));
			return;
		}
		final OfflinePlayer op = Bukkit.getOfflinePlayer(getOwner());
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(op, p.getWorld());
		if (!PermissionUT.has(p, "morevaultplus.open.bypass")) {
			if (Core.thereWorldGuard && !WorldGuardHook.canOpen(p)) {
				MessageUT.plmessage(p, Locales.getValue("worldguard-open"));
				return;
			}
			if (p.getVelocity().getY() > 0.0 || !p.isOnGround() || !getBlocks(p.getLocation().getBlock(), 1)) {
				MessageUT.plmessage(p, Locales.getValue("open-place"));
				return;
			}
		}
		if (StringUT.d(getTitle()).contains("1.13") && ReflectionUT.NUMBER_VERSION < 113) {
			MessageUT.plmessage(p, Locales.getValue("inventory-notloaded"));
			return;
		}
		if (inv == null) {
			if (op.isOnline()) {
				setRow(MoreVaultAPI.getSpace(cp) / 9);
			} else {
				setRow(6);
			}
			build();
			fromString(Datamanager.vault_table.getValue(identifier, "Content"));
		}
		if (Bukkit.getOfflinePlayer(owner).isOnline() && MoreVaultAPI.getSpace(cp) != inv.getSize()) {
			setRow(MoreVaultAPI.getSpace(cp) / 9);
			build();
		}
		if (pu == null) {
			pu = VaultMenu.getPlaceholder(p, Bukkit.getOfflinePlayer(getOwner()), this);
		}
		pu.add("money", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return MathUT.format(EconomyUT.getRawBal(p));
			}
		});
		pu.add("exp", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new StringBuilder().append(PlayerUT.getTotalExperience(p)).toString();
			}
		});
		if (p.isOnGround()) {
			p.setVelocity(new Vector());
		}
		pu.add("prevvault", new StringBuilder().append(id - 1).toString());
		pu.add("nextvault", new StringBuilder().append(id + 1).toString());
		setup();
		final PlaceholderUT cpu = pu;
		if (ConfigManager.nobottom) {
			for (int i = inv.getSize() - 9; i < inv.getSize(); ++i) {
				this.setItem(i, EVault.BorderItem).addAction(new Action() {
					@Override
					public void act() {
						EVault.setCloseAction(EVault.this, new Action() {
							@Override
							public void act() {
								EVault.this.save();
							}
						});
						MainMenu.open(p, Bukkit.getOfflinePlayer(EVault.this.getOwner()), 1);
					}
				});
			}
		}
		if (ConfigManager.vaultn
				&& (PermissionUT.has(p, "morevaultplus.open.other") || getOwner().equals(p.getUniqueId()))) {
			if (id <= 1) {
				this.setItem(inv.getSize() - 9, EVault.PrevPageMaxItem.clone(), pu);
			} else if (id > 1) {
				this.setItem(inv.getSize() - 9, EVault.PrevPageItem.clone(), pu).addAction(new Action() {
					@Override
					public void act() {
						EVault.setCloseAction(EVault.this, new Action() {
							@Override
							public void act() {
								EVault.this.save();
							}
						});
						final EVault ev = Datamanager.getVaultByID(EVault.this.getOwner(), EVault.this.id - 1);
						ev.openVault(null, p);
						ev.setCloseaction(new Action() {
							@Override
							public void act() {
								if (ConfigManager.autoback) {
									MainMenu.open(p, Bukkit.getOfflinePlayer(EVault.this.getOwner()), 1);
								}
								ev.save();
							}
						});
					}
				});
			}
			if (id < MoreVaultAPI.getOwnedVault(op, cp)) {
				this.setItem(inv.getSize() - 1, EVault.NextPageItem.clone(), pu).addAction(new Action() {
					@Override
					public void act() {
						EVault.setCloseAction(EVault.this, new Action() {
							@Override
							public void act() {
								EVault.this.save();
							}
						});
						final EVault ev = Datamanager.getVaultByID(EVault.this.getOwner(), EVault.this.id + 1);
						ev.openVault(null, p);
						ev.setCloseaction(new Action() {
							@Override
							public void act() {
								if (ConfigManager.autoback) {
									MainMenu.open(p, Bukkit.getOfflinePlayer(EVault.this.getOwner()), 1);
								}
								ev.save();
							}
						});
					}
				});
			} else {
				this.setItem(inv.getSize() - 1, EVault.NextPageMaxItem.clone(), pu);
			}
		}
		pu.add("cancel", "cancel");
		if (ConfigManager.vaultd) {
			this.setItem(inv.getSize() - 6, EVault.BalanceItem.clone(), pu)
					.addAction(new Action(new ClickType[] { ClickType.LEFT }) {
						@Override
						public void act() {
							EVault.setCloseAction(EVault.this, new Action() {
								@Override
								public void act() {
									EVault.this.save();
								}
							});
							MessageUT.plmessage(p, Locales.getValue("deposit_vault"), cpu);
							p.closeInventory();
							PlayerListener.cachebalance.put(p.getUniqueId(), EVault.this);
						}
					}).addAction(new Action(new ClickType[] { ClickType.RIGHT }) {
						@Override
						public void act() {
							EVault.setCloseAction(EVault.this, new Action() {
								@Override
								public void act() {
									EVault.this.save();
								}
							});
							MessageUT.plmessage(p, Locales.getValue("withdraw_vault"), cpu);
							p.closeInventory();
							PlayerListener.cachewbalance.put(p.getUniqueId(), EVault.this);
						}
					});
		}
		if (ConfigManager.vaulted) {
			this.setItem(inv.getSize() - 4, EVault.ExpItem.clone(), pu)
					.addAction(new Action(new ClickType[] { ClickType.LEFT }) {
						@Override
						public void act() {
							EVault.setCloseAction(EVault.this, new Action() {
								@Override
								public void act() {
									EVault.this.save();
								}
							});
							cpu.add("amount", new StringBuilder().append(EVault.this.getExp()).toString());
							MessageUT.plmessage(p, Locales.getValue("exp_deposit_vault"), cpu);
							p.closeInventory();
							PlayerListener.cacheexp.put(p.getUniqueId(), EVault.this);
						}
					}).addAction(new Action(new ClickType[] { ClickType.RIGHT }) {
						@Override
						public void act() {
							EVault.setCloseAction(EVault.this, new Action() {
								@Override
								public void act() {
									EVault.this.save();
								}
							});
							cpu.add("amount", new StringBuilder().append(EVault.this.getExp()).toString());
							MessageUT.plmessage(p, Locales.getValue("exp_withdraw_vault"), cpu);
							p.closeInventory();
							PlayerListener.cachewexp.put(p.getUniqueId(), EVault.this);
						}
					});
		}
		if (ConfigManager.vaultsort) {
			this.setItem(inv.getSize() - 5, EVault.SortItem.clone(), pu).addAction(new Action() {
				@Override
				public void act() {
					final Inventory temp = Bukkit.createInventory((InventoryHolder) null,
							EVault.this.getInv().getSize());
					ItemStack[] contents;
					for (int length = (contents = EVault.this.getInv().getContents()).length, j = 0; j < length; ++j) {
						final ItemStack i = contents[j];
						if (i != null && !i.getType().equals(Material.AIR)) {
							if (!GUIMenu.isSecured(i)) {
								temp.addItem(i);
								EVault.this.getInv().remove(i);
							}
						}
					}
					Sorting.sortInventory(temp);
					ItemStack[] contents2;
					for (int length2 = (contents2 = temp.getContents()).length, k = 0; k < length2; ++k) {
						final ItemStack i = contents2[k];
						if (i != null && !i.getType().equals(Material.AIR)) {
							EVault.this.getInv().addItem(i);
						}
					}
				}
			});
		}
		MessageUT.plmessage(p, Locales.getValue("vault_open"));
		MenuAnimation.addAnimated(this);
		open(p);
	}

	public EVault(final UUID owner, final int id) {
		this(owner + "<@>" + id);
	}

	private static double safe(final String str) {
		if (str == null || str.isEmpty()) {
			return 0.0;
		}
		try {
			final double d = Double.parseDouble(str);
			return d;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public EVault(final String identifier) {
		description = null;
		balance = 0.0;
		exp = 0.0;
		setStealable(true);
		setEditable(!ConfigManager.onlytake);
		this.identifier = identifier;
		setName("VaultMenu");
		setId(MathUT.strInt(identifier.split("<@>")[1]));
		setOwner(UUID.fromString(identifier.split("<@>")[0]));
		final OfflinePlayer op = Bukkit.getOfflinePlayer(getOwner());
		if (!Datamanager.vault_table.getLoadedValue().containsKey(identifier)) {
			final PlaceholderUT pu = new PlaceholderUT();
			pu.add("id", new StringBuilder().append(id).toString());
			setTitle(pu.t(EVault.defaulttitle));
			setIcon(EVault.defaulticon);
			if (op.isOnline()) {
				final CustomPermission cp = MoreVaultAPI.getCustomPermission(op, Bukkit.getWorlds().get(0));
				setRow(MoreVaultAPI.getSpace(cp) / 9);
				if (StringUT.d(title).contains("1.13")) {
					if (ReflectionUT.NUMBER_VERSION >= 113) {
						build();
					}
				} else {
					build();
				}
			}
		} else {
			final PlaceholderUT pu = new PlaceholderUT();
			pu.add("id", new StringBuilder().append(id).toString());
			setTitle(pu.t(Datamanager.vault_table.getValue(identifier, "Name")));
			if (op.isOnline()) {
				final CustomPermission cp = MoreVaultAPI.getCustomPermission(op, Bukkit.getWorlds().get(0));
				setRow(MoreVaultAPI.getSpace(cp) / 9);
				if (StringUT.d(title).contains("1.13")) {
					if (ReflectionUT.NUMBER_VERSION >= 113) {
						build();
						fromString(Datamanager.vault_table.getValue(identifier, "Content"));
					}
				} else {
					build();
					fromString(Datamanager.vault_table.getValue(identifier, "Content"));
				}
			}
			setDescription(ArrayUT.listFromString(Datamanager.vault_table.getValue(identifier, "Description")));
			setBalance(safe(Datamanager.vault_table.getValue(identifier, "Balance")));
			setExp(safe(Datamanager.vault_table.getValue(identifier, "Exp")));
			setIcon(Datamanager.vault_table.getValue(identifier, "Icon"));
			if (MathUT.strInt(icon) > 0) {
				final EMaterial em = EMaterial.fromString(icon);
				if (ReflectionUT.NUMBER_VERSION < 113) {
					setIcon(String.valueOf(em.m) + ":" + em.data);
				} else {
					setIcon(String.valueOf(em.toString()) + ":" + em.data);
				}
				save();
			}
		}
	}

	public void save() {
		Datamanager.save(this);
	}

	public String getContent() {
		return toString();
	}

	@Override
	public void setOwner(final UUID owner) {
		this.owner = owner;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getSpace() {
		if (inv != null) {
			return space = row * 9;
		}
		final OfflinePlayer op = Bukkit.getOfflinePlayer(owner);
		if (op.isOnline()) {
			final CustomPermission cp = MoreVaultAPI.getCustomPermission(op, Bukkit.getWorlds().get(0));
			return MoreVaultAPI.getSpace(cp);
		}
		return -1;
	}

	public void setSpace(final int space) {
		setRow((this.space = space) / 9);
	}

	public int getUsed() {
		if (inv == null) {
			return 0;
		}
		int used = 0;
		ItemStack[] contents;
		for (int length = (contents = inv.getContents()).length, j = 0; j < length; ++j) {
			final ItemStack i = contents[j];
			if (i != null && !i.getType().equals(Material.AIR) && !GUIMenu.isSecured(i)) {
				++used;
			}
		}
		return used;
	}

	public int getItemsAmount() {
		if (inv == null) {
			return 0;
		}
		int used = 0;
		ItemStack[] contents;
		for (int length = (contents = inv.getContents()).length, j = 0; j < length; ++j) {
			final ItemStack i = contents[j];
			if (i != null && !i.getType().equals(Material.AIR) && !GUIMenu.isSecured(i)) {
				used += i.getAmount();
			}
		}
		return used;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(final String icon) {
		this.icon = icon;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(final double balance) {
		this.balance = balance;
	}

	public void addBalance(final double balance) {
		setBalance(getBalance() + balance);
	}

	public double getExp() {
		return exp;
	}

	public void setExp(final double exp) {
		this.exp = exp;
	}

	public void addExp(final double exp) {
		setExp(getExp() + exp);
	}

	public String getOwnerName() {
		return Bukkit.getOfflinePlayer(getOwner()).getName();
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(final List<String> description) {
		this.description = description;
	}

	public HashMap<String, Object> getValues() {
		final HashMap<String, Object> columns = new HashMap<>();
		for (final String c : Datamanager.vault_table.getColumns().keySet()) {
			Object value = "";
			switch (c.toLowerCase()) {
			case "description": {
				value = ArrayUT.stringFromList(getDescription());
				break;
			}
			case "identifier": {
				value = getIdentifier();
				break;
			}
			case "balance": {
				value = getBalance();
				break;
			}
			case "id": {
				value = getId();
				break;
			}
			case "exp": {
				value = getExp();
				break;
			}
			case "icon": {
				value = getIcon();
				break;
			}
			case "name": {
				value = getTitle();
				break;
			}
			case "owner": {
				value = getOwner().toString();
				break;
			}
			case "space": {
				value = getSpace();
				break;
			}
			case "content": {
				value = getContent();
				break;
			}
			default:
				break;
			}
			columns.put(c, value);
		}
		return columns;
	}

	public static void setCloseAction(final EVault eVault, final Action closeaction) {
		eVault.closeaction = closeaction;
	}
}
