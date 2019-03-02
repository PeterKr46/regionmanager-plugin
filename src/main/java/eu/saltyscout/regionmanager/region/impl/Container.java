package eu.saltyscout.regionmanager.region.impl;






import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.region.FlagContainer;
import eu.saltyscout.regionmanager.region.MemberContainer;
import eu.saltyscout.regionmanager.region.OwnerContainer;
import eu.saltyscout.regionmanager.user.AccessToken;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implements all container behaviour shared among all region types.
 */
public abstract class Container implements MemberContainer, OwnerContainer, FlagContainer {
    private List<AccessToken> owners;
    private List<AccessToken> members;
    private HashMap<String, Flag> flags;
    
    Container() {
        this.flags = new HashMap<>();
        this.owners = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    @Override
    public Flag getFlag(@Nonnull String flag) {
        checkNotNull(flag);
        Flag f = flags.get(flag.toLowerCase());
        if(f != null) {
            f = f.clone();
        }
        return f;
    }

    @Override
    public <T> T getFlag(@Nonnull String flag, @Nonnull Class<? extends Flag<T>> clazz) {
        checkNotNull(flag);
        checkNotNull(clazz);
        return (T) flags.get(flag.toLowerCase()).getValue();
    }

    @Override
    public <T> T getFlag(@Nonnull String flag, @Nonnull Class<? extends Flag<T>> clazz, @Nonnull T defValue) {
        checkNotNull(flag);
        checkNotNull(clazz);
        checkNotNull(defValue);
        Flag f = flags.get(flag.toLowerCase());
        if(f == null) {
            return defValue;
        } else {
            return (T) f.getValue();
        }
    }


    @Override
    public Set<String> getSetFlagIds() {
        return ImmutableSet.copyOf(flags.keySet());
    }

    @Override
    public Map<String,Flag> getSetFlags() {
        ImmutableMap.Builder<String, Flag> builder = ImmutableMap.builder();
        flags.forEach((id, flag) -> builder.put(id, flag.clone()));
        return builder.build();
    }
    
    @Override
    public void setFlag(@Nonnull String id, @Nonnull Flag flag) {
        checkNotNull(flag);
        checkNotNull(id);
        flags.put(id, flag.clone());
    }
    
    @Override
    public boolean hasFlag(@Nonnull String flag) {
        checkNotNull(flag);
        return flags.containsKey(flag.toLowerCase());
    }
    
    @Override
    public Flag clearFlag(@Nonnull String flag) {
        checkNotNull(flag);
        return flags.remove(flag.toLowerCase());
    }
    
    @Override
    public void clearFlags() {
        flags.clear();
    }
    
    @Override
    public int numFlags() {
        return flags.size();
    }
    
    
    @Override
    public boolean addOwner(@Nonnull AccessToken owner) {
        checkNotNull(owner);
        
        if (owners.stream().noneMatch(member -> member.isEqual(owner))) {
            owners.add(owner);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeOwner(@Nonnull AccessToken player) {
        checkNotNull(player);
        return owners.removeIf(owner -> owner.isEqual(player));
    }
    
    @Override
    public boolean isOwner(@Nonnull AccessToken player) {
        checkNotNull(player);
        return owners.stream().anyMatch(player::inherits);
    }
    
    @Override
    public int numOwners() {
        return owners.size();
    }
    
    @Override
    public Set<AccessToken> getOwners() {
        return ImmutableSet.copyOf(owners);
    }
    
    @Override
    public boolean removeMember(@Nonnull AccessToken member) {
        checkNotNull(member);
        return members.removeIf(m -> m.isEqual(member));
    }
    
    @Override
    public boolean addMember(@Nonnull AccessToken player) {
        checkNotNull(player);
        if (members.stream().noneMatch(member -> member.isEqual(player))) {
            members.add(player);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isMember(@Nonnull AccessToken player) {
        checkNotNull(player);
        return isOwner(player) || members.stream().anyMatch(player::inherits);
    }
    
    @Override
    public int numMembers() {
        return members.size();
    }
    
    @Override
    public Set<AccessToken> getMembers() {
        return ImmutableSet.copyOf(members);
    }
    
}
