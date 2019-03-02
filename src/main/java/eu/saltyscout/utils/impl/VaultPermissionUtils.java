package eu.saltyscout.utils.impl;

import com.google.common.collect.ImmutableSet;
import eu.saltyscout.regionmanager.RegionManagerPlugin;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;

/**
 * Created by Peter on 04.04.2017.
 */
public class VaultPermissionUtils extends PermissionUtils {

    public static void init() throws ClassNotFoundException, NoClassDefFoundError {
        instance = new VaultPermissionUtils();
        RegionManagerPlugin.getInstance().getLogger().info("Vault Permissions hook initialized (Group Support)");
    }

    private net.milkbowl.vault.permission.Permission permission;
    private VaultPermissionUtils() throws ClassNotFoundException, NoClassDefFoundError {
        setupPermissions();
    }

    private boolean setupPermissions() throws ClassNotFoundException, NoClassDefFoundError {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    @Override
    protected boolean _hasPermission(OfflinePlayer player, String perm) {
        return player.isOnline() && permission.has(player.getPlayer(), perm);
    }

    @Override
    protected boolean _hasPermission(String group, String perm) {
        return permission.groupHas((String) null, group, perm);
    }

    @Override
    protected Set<AccessToken> _getGroups(OfflinePlayer player) {
        Set<AccessToken> result;
        if(player.isOnline()) {
            result = ImmutableSet.copyOf(Arrays.stream(permission.getPlayerGroups(player.getPlayer())).map(group -> AccessTokenFactory.wrap("group:" + group)).iterator());
        } else {
            result = ImmutableSet.of();
        }
        return result;
    }
}
