package eu.saltyscout.regionmanager.user.impl;



import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Peter on 07-Nov-16.
 */
class PlayerToken implements AccessToken {
    private UUID uuid;
    PlayerToken(@Nonnull UUID obj) {
        checkNotNull(obj);
        this.uuid = new UUID(obj.getMostSignificantBits(), obj.getLeastSignificantBits());
    }

    PlayerToken(long mostSig, long leastSig) {
        this.uuid = new UUID(mostSig, leastSig);
    }

    @Override
    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return (player.isOnline() ? "" : ChatColor.RED ) + player.getName();
    }

    @Override
    public String dump() {
        return uuid.toString();
    }

    @Override
    public boolean isEqual(@Nullable AccessToken other) {
        return other != null && other instanceof PlayerToken && ((PlayerToken) other).uuid.equals(uuid);
    }

    @Override
    public boolean inherits(@Nullable AccessToken other) {
        boolean result;
        if(other == null) {
            result = false;
        } else {
            if(other instanceof PlayerToken) {
                // Check if we are the same player as the other
                result = ((PlayerToken) other).uuid.equals(uuid);
            } else if(other instanceof PermissionToken) {
                // Check if we have the permssion of the token
                result = PermissionUtils.hasPermission(Bukkit.getOfflinePlayer(uuid), ((PermissionToken) other).getPermission());
            } else if(other instanceof VaultGroupToken) {
                // Check if any of this Player's groups inherit from the given group
                result = PermissionUtils.getGroups(Bukkit.getOfflinePlayer(uuid)).stream().anyMatch(token -> token.inherits(other));
            } else {
                // Default to false if this is an unrecognized type.
                // Like, really. Who would even do that..
                result = false;
            }
        }
        return result;
    }
    
    @Override
    public AccessToken clone() {
        return new PlayerToken(uuid);
    }
    
}
