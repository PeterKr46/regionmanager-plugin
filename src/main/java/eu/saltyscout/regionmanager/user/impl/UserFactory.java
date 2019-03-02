package eu.saltyscout.regionmanager.user.impl;



import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Peter on 04.04.2017.
 */
public class UserFactory extends AccessTokenFactory {

    public static void init() {
        instance = new UserFactory();
    }

    @Override
    protected AccessToken _getServerMember() {
        return SERVER;
    }

    @Override
    protected AccessToken _wrap(@Nonnull Object o) {
        checkNotNull(o);

        if (String.valueOf(o).equalsIgnoreCase("_SERVER")) {
            return SERVER;
        }
        if (o instanceof AccessToken) {
            return (AccessToken) o;
        }
        if (o instanceof OfflinePlayer) {
            return new PlayerToken(((OfflinePlayer) o).getUniqueId());
        }
        if (Bukkit.getOfflinePlayer(o.toString()).hasPlayedBefore()) {
            return new PlayerToken(Bukkit.getOfflinePlayer(o.toString()).getUniqueId());
        }
        try {
            UUID uuid = UUID.fromString(o.toString());
            try {
                return new PlayerToken(uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {

        }
        if(o.toString().startsWith("group:")) {
            return new VaultGroupToken(o.toString());
        }
        return new PermissionToken(o.toString());
    }
}
