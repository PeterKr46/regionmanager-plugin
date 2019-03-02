package eu.saltyscout.utils.impl;



import com.google.common.collect.ImmutableSet;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;

import java.util.Set;

/**
 * Created by Peter on 04.04.2017.
 */
public class DefaultPermissionUtils extends PermissionUtils {

    public static void init() {
        instance = new DefaultPermissionUtils();
    }

    @Override
    protected boolean _hasPermission(OfflinePlayer player, String perm) {
        return player.isOnline() && player.getPlayer().hasPermission(perm);
    }

    @Override
    protected boolean _hasPermission(String group, String perm) {
        return false;
    }

    @Override
    protected Set<AccessToken> _getGroups(OfflinePlayer player) {
        return ImmutableSet.of();
    }
}
