package eu.saltyscout.regionmanager.user.impl;



import eu.saltyscout.regionmanager.user.AccessToken;

import javax.annotation.Nullable;

/**
 * Created by Peter on 07-Nov-16.
 */
class VaultGroupToken implements AccessToken {
    private String value;

    VaultGroupToken(String obj) {
        while (obj.startsWith("group:")) {
            obj = obj.substring(6);
        }
        value = obj;
    }

    String getGroup() {
        return value;
    }

    @Override
    public String getName() {
        return "group:" + value;
    }

    @Override
    public String dump() {
        return "group:" + String.valueOf(value);
    }

    @Override
    public boolean isEqual(@Nullable AccessToken other) {
        return other != null && other instanceof VaultGroupToken && ((VaultGroupToken) other).getGroup().equalsIgnoreCase(getGroup());
    }

    @Override
    public boolean inherits(@Nullable AccessToken other) {
        // TODO: For now, groups will not inherit from any AccessTokens.
        return isEqual(other);
    }
    
    @Override
    public AccessToken clone() {
        return new VaultGroupToken(value);
    }
    
}
