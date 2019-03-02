package eu.saltyscout.regionmanager.user.impl;




import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.utils.PermissionUtils;

import javax.annotation.Nullable;

/**
 * Created by Peter on 07-Nov-16.
 */
class PermissionToken implements AccessToken {
    private String value;

    PermissionToken(String obj) {
        while (obj.startsWith("perm:")) {
            obj = obj.substring(5);
        }
        value = obj.toLowerCase();
    }
    
    public String getPermission() {
        return String.valueOf(value);
    }

    @Override
    public String getName() {
        return "perm:" + value;
    }

    @Override
    public String dump() {
        return "perm:" + String.valueOf(value);
    }

    @Override
    public boolean isEqual(@Nullable AccessToken other) {
        return other != null && other instanceof PermissionToken && ((PermissionToken) other).getPermission().equalsIgnoreCase(getPermission());
    }

    @Override
    public boolean inherits(@Nullable AccessToken other) {
        boolean result;
        if(other != null) {
            if(other instanceof PermissionToken) {
                // We can inherit from other PermissionTokens:
                // If they are equivalent, they inherit one another.
                // Alternatively, if own Permission implies other Permission, we inherit the other.
                result = ((PermissionToken) other).getPermission().equalsIgnoreCase(getPermission()) || PermissionUtils.implies(getPermission(), ((PermissionToken) other).getPermission());
            } else if(other instanceof VaultGroupToken) {
                // We can inherit from VaultGroupTokens:
                result = PermissionUtils.hasPermission(((VaultGroupToken) other).getGroup(), getPermission());
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }
    
    @Override
    public AccessToken clone() {
        return new PermissionToken(value);
    }
    
}
