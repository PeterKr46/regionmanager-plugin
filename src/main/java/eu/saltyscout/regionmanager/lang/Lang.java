package eu.saltyscout.regionmanager.lang;


import eu.saltyscout.regionmanager.RegionManagerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Peter on 17-Nov-16.
 */
public class Lang {
    
    
    public synchronized static boolean load(@Nonnull Configuration configuration) {
        checkNotNull(configuration);
        boolean result;
        if(!(configuration.getString("version", "outdated").equalsIgnoreCase(RegionManagerPlugin.getInstance().getDescription().getVersion()))) {
            result = false;
        } else {
            for (Field field : Lang.class.getDeclaredFields()) {
                if (!field.getType().isArray()) {
                    try {
                        String str = configuration.getString(field.getName(), field.getName());
                        str = ChatColor.translateAlternateColorCodes('$', str);
                        field.set(null, str);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        List<String> cVal = configuration.getStringList(field.getName());
                        if (cVal != null) {
                            cVal = cVal.stream().map(str -> ChatColor.translateAlternateColorCodes('$', str)).collect(Collectors.toList());
                            field.set(null, cVal.toArray(new String[cVal.size()]));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            result = true;
        }
        return result;
    }
    
    public static String ENDERPEARL_DENY_MESSAGE,
            RESULT_IS_AN_OWNER,
            RESULT_NOT_AN_OWNER,
            ALREADY_AN_OWNER,
            OWNER_NOT_FOUND,
            OWNER_ADDED,
            OWNER_REMOVED,
            BUILD_DENY_MESSAGE,
            ENTER_DENY_MESSAGE,
            EXIT_DENY_MESSAGE,
            PVP_DENY_MESSAGE_SELF,
            PVP_DENY_MESSAGE_OTHER,
            CONTAINER_DENY_MESSAGE,
            REDSTONE_DENY_MESSAGE,
            GATE_DENY_MESSAGE,
            TELEPORT_FROM_DENY_MESSAGE,
            TELEPORT_TO_DENY_MESSAGE,
            GAMEMODE_CHANGED_MESSAGE,
            TELEPORTED_MESSAGE,
            ITEM_DROP_DENY_MESSAGE,
            COMMAND_DENY_MESSAGE,
            REGION_NOT_FOUND,
            MEMBER_NOT_FOUND,
            ALREADY_A_MEMBER,
            REGION_REMOVED,
            FLAG_REMOVED,
            FLAG_UPDATED,
            MEMBER_ADDED,
            REGION_DEFINED,
            REGION_REDEFINED,
            REGION_SELECTED,
            REGION_RENAMED,
            REGIONS_SAVED,
            PRIORITY_UPDATED,
            RELOAD_SUCCESSFUL,
            REGION_SUMMARY_LIST,
            REGION_SUMMARY_INFO,
            FLAGS_AT_LOCATION,
            REGIONS_AT_LOCATION,
            SHOWING_PAGE_X_OF_Y,
            ATTR_OWNERS,
            ATTR_NAME,
            ATTR_TYPE,
            ATTR_WORLD,
            ATTR_APPROXIMATE_SIZE,
            ATTR_PRIORITY,
            ATTR_MEMBERS,
            MEMBER_SUMMARY,
            ATTR_FLAGS,
            FLAG_SUMMARY_REGION,
            FLAG_SUMMARY_LIST,
            PERMISSION_DENIED,
            UNRECOGNIZED_COMMAND,
            RESULT_NOT_A_MEMBER,
            RESULT_IS_A_MEMBER,
            NUM_WORLDGUARD_REGIONS_RESTORED,
            UNKNOWN_FLAG,
            INCORRECT_FLAG_TYPE,
            INCOMPLETE_SELECTION,
            PLAYER_ONLY_COMMAND,
            CANNOT_EDIT_GLOBAL,
            CANNOT_SELECT_GLOBAL,
            REGION_ALREADY_EXISTS,
            INCOMPLETE_BOOLE_FORMAT,
            NO_WORLDGUARD_DATA,
            ERROR_OCCURRED,
            COMMAND_SUMMARY,
            COMMAND_DESCRIPTION,
            FLAG_IS_READ_ONLY,
            FLAG_SUMMARY,
            FLAG_DESCRIPTION,
            FLAG_DEPENDENCY_NOT_FULFILLED,
            BEGINNING_WORLDGUARD_RESTORE,
            FLAG_PURGE_COMPLETE,
            BEGINNING_FLAG_PURGE,
            MEMBER_REMOVED,
            MEMBER_REMOVED_WARN_MEMBER,
            OWNER_CHECK_NOT_ONLINE,
            MEMBER_CHECK_NOT_ONLINE;
    
}
