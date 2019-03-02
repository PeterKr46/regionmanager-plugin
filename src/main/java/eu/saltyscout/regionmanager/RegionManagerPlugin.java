package eu.saltyscout.regionmanager;

import eu.saltyscout.regionmanager.command.SpecificRegionCommandExecutor;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.flag.handlers.EntryEventAdapter;
import eu.saltyscout.regionmanager.flag.handlers.combat.InvincibleListener;
import eu.saltyscout.regionmanager.flag.handlers.combat.MobDamageListener;
import eu.saltyscout.regionmanager.flag.handlers.combat.PvPListener;
import eu.saltyscout.regionmanager.flag.handlers.combat.SafePvPListener;
import eu.saltyscout.regionmanager.flag.handlers.misc.FarewellListener;
import eu.saltyscout.regionmanager.flag.handlers.misc.GameModeListener;
import eu.saltyscout.regionmanager.flag.handlers.misc.GreetListener;
import eu.saltyscout.regionmanager.flag.handlers.misc.ItemDropListener;
import eu.saltyscout.regionmanager.flag.handlers.mob.EnderManGriefListener;
import eu.saltyscout.regionmanager.flag.handlers.mob.MobSpawnListener;
import eu.saltyscout.regionmanager.flag.handlers.protect.BuildListener;
import eu.saltyscout.regionmanager.flag.handlers.protect.InteractListener;
import eu.saltyscout.regionmanager.flag.handlers.protect.physics.*;
import eu.saltyscout.regionmanager.flag.handlers.restrict.*;
import eu.saltyscout.regionmanager.flag.type.BooleanFlag;
import eu.saltyscout.regionmanager.flag.type.GameModeFlag;
import eu.saltyscout.regionmanager.flag.type.LocationFlag;
import eu.saltyscout.regionmanager.flag.type.StringFlag;
import eu.saltyscout.regionmanager.flag.type.list.MaterialListFlag;
import eu.saltyscout.regionmanager.flag.type.list.StringListFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.impl.RegionRegistry;
import eu.saltyscout.regionmanager.user.impl.UserFactory;
import eu.saltyscout.utils.PlayerNotification;
import eu.saltyscout.utils.impl.DefaultPermissionUtils;
import eu.saltyscout.utils.impl.VaultPermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class RegionManagerPlugin extends JavaPlugin {

    private static RegionManagerPlugin instance;
    private long lastBackup;

    public static RegionManagerPlugin getInstance() {
        return instance;
    }

    private long backupDelay = 7200;

    public long getBackupDelay() {
        return backupDelay;
    }


    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }
        // Load the backup delay
        backupDelay = getConfig().getLong("backup-delay", backupDelay);
        // Load the last backup timestamp
        lastBackup = getConfig().getLong("last-backup", 0L);
        instance = this;
        // Load wether or not to use Subtitles
        PlayerNotification.setUseSubtitles(getConfig().getBoolean("use-subtitle-notifications", true));
        // Load the message config
        if(!Lang.load(YamlConfiguration.loadConfiguration(langFile))) {
            saveResource("lang.yml", true);
            Lang.load(YamlConfiguration.loadConfiguration(langFile));
        }
        RegionRegistry.init();
        UserFactory.init();
        try {
            VaultPermissionUtils.init();
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            DefaultPermissionUtils.init();
            RegionManagerPlugin.getInstance().getLogger().info("Falling back to Default Permissions hook. (No Group Support)");
        }
        try {
            FlagSet.flush();
            FlagSet.register("build", "Prevents or allows building. If this flag is set, non-members may never build. Owners may always build. Members may build depending on the flag value.", BooleanFlag.class);
            FlagSet.register("mob-spawning", "Prevents all mobs from spawning.", BooleanFlag.class);
            FlagSet.register("mob-damage", "Prevents all direct damage to players caused by mobs.", BooleanFlag.class);
            FlagSet.register("fire-spread", "If false: Prevents fire spread and decay.", BooleanFlag.class);
            FlagSet.register("water-flow", "If false: Prevents water flow.", BooleanFlag.class);
            FlagSet.register("lava-flow", "If false: Prevents water flow", BooleanFlag.class);
            FlagSet.register("enderman-protect", "If true: Prevents endermen from picking up blocks.", BooleanFlag.class);
            FlagSet.register("creeper-explosion", "If false: Prevents creeper explosions.", BooleanFlag.class);
            FlagSet.register("other-explosion", "If false: Prevents other explosions.", BooleanFlag.class);
            FlagSet.register("entry", "...", BooleanFlag.class);
            FlagSet.register("entry-deny-text", "...", StringFlag.class);
            FlagSet.register("exit", "...", BooleanFlag.class);
            FlagSet.register("exit-deny-text", "...", StringFlag.class);
            FlagSet.register("teleport", "...", LocationFlag.class);
            FlagSet.register("greeting", "...", StringFlag.class);
            FlagSet.register("farewell", "...", StringFlag.class);
            FlagSet.register("pvp", "...", BooleanFlag.class);
            FlagSet.register("safe-pvp", "...", BooleanFlag.class);
            FlagSet.register("snow-melt", "...", BooleanFlag.class);
            FlagSet.register("ice-melt", "...", BooleanFlag.class);
            FlagSet.register("leaf-decay", "...", BooleanFlag.class);
            FlagSet.register("invincible", "...", BooleanFlag.class);
            FlagSet.register("teleport-from", "...", BooleanFlag.class);
            FlagSet.register("teleport-to", "...", BooleanFlag.class);
            FlagSet.register("enderpearl", "...", BooleanFlag.class);
            FlagSet.register("open-container", "...", BooleanFlag.class);
            FlagSet.register("open-gate", "...", BooleanFlag.class);
            FlagSet.register("edit-redstone", "...", BooleanFlag.class);
            FlagSet.register("gamemode", "...", GameModeFlag.class);
            FlagSet.register("item-drop", "...", BooleanFlag.class);
            FlagSet.register("allowed-cmds", "...", StringListFlag.class);
            FlagSet.register("blocked-cmds", "...", StringListFlag.class);
            FlagSet.register("ice-form", "...", BooleanFlag.class);
            FlagSet.register("snow-fall", "...", BooleanFlag.class);
            FlagSet.register("block-break", "...", BooleanFlag.class);
            FlagSet.register("block-break-override", "...", MaterialListFlag.class);
            FlagSet.register("block-place-override", "...", MaterialListFlag.class);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (!(getDataFolder().exists() || getDataFolder().mkdir())) {
            getLogger().info("Failed to load data folder! Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // Regions are loaded with a delay of one tick.
        // This is done to ensure that all plugins have had time to register their flags.
        Bukkit.getScheduler().scheduleSyncDelayedTask(this,
                () -> {
                        try {
                            RegionRegistry.initialize(getDataFolder());
                        } catch (Exception e) {
                            e.printStackTrace();
                            getLogger().info("Failed to load regions! Disabling...");
                            Bukkit.getPluginManager().disablePlugin(this);
                        }
                    }
                );
        SpecificRegionCommandExecutor regionCommandExecutor = new SpecificRegionCommandExecutor();
        Bukkit.getPluginCommand("regionmanager:region").setExecutor(regionCommandExecutor);
        Bukkit.getPluginCommand("regionmanager:region").setTabCompleter(regionCommandExecutor);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                this,
                () -> {
                    try {
                        RegionRegistry.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                20 * 120,
                20 * 120
        );
        // This one is responsible for RegionEnter/LeaveEvents. Don't stop it pls
        Bukkit.getPluginManager().registerEvents(new EntryEventAdapter(), this);

        Bukkit.getPluginManager().registerEvents(new BuildListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnderManGriefListener(), this);
        Bukkit.getPluginManager().registerEvents(new ExplosionListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobSpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new PvPListener(), this);
        Bukkit.getPluginManager().registerEvents(new SafePvPListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntryListener(), this);
        Bukkit.getPluginManager().registerEvents(new ExitListener(), this);
        Bukkit.getPluginManager().registerEvents(new GreetListener(), this);
        Bukkit.getPluginManager().registerEvents(new FarewellListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlowListener(), this);
        Bukkit.getPluginManager().registerEvents(new LeafDecayListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockMeltListener(), this);
        Bukkit.getPluginManager().registerEvents(new InvincibleListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnderpearlListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new AllowTeleportListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameModeListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemDropListener(), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new WinterListener(), this);
    }

    public void onDisable() {
        try {
            RegionRegistry.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getConfig().set("last-backup", lastBackup);
        saveConfig();
        instance = null;
    }


    public long getLastBackup() {
        return lastBackup;
    }

    public void setBackupMade() {
        lastBackup = System.currentTimeMillis() / 1000;
    }
}