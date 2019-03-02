package eu.saltyscout.regionmanager.region.impl;






import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import eu.saltyscout.booleregion.region.BooleRegion;
import eu.saltyscout.booleregion.region.BooleRegionSelector;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.region.shape.ShapeFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Peter on 16-Nov-16.
 */
public class ShapedRegion extends BasicRegion implements eu.saltyscout.regionmanager.region.ShapedRegion {
    private String name;
    private com.sk89q.worldedit.regions.Region shape = null;
    private RegionType shapeType = RegionType.UNKNOWN;
    
    ShapedRegion(ConfigurationSection section) throws Exception {
        super.load(section);
    }
    
    ShapedRegion(String name, com.sk89q.worldedit.regions.Region selection) throws Exception {
        this.name = name;
        setShape(selection.clone());
    }

    @Override
    public boolean redefine( com.sk89q.worldedit.regions.Region weRegion) {
        return setShape(weRegion);
    }


    private boolean setShape(@Nonnull  com.sk89q.worldedit.regions.Region weShape) {
        checkNotNull(weShape);
        shape = weShape.clone();
        shapeType = ShapeFactory.getRegionType(shape);
        return true;
    }

    private boolean hasShape() {
        return shape != null;
    }

    @Override
    public com.sk89q.worldedit.regions.Region getShape() {
        return hasShape() ? shape.clone() : null;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean rename(@Nonnull String newName) {
        checkNotNull(newName);
        while (newName.startsWith("global:")) {
            newName = newName.substring(7);
        }
        if (RegionRegistry.exists(newName)) {
            return false;
        } else {
            RegionRegistry.reregister(this, newName);
            this.name = newName;
            return true;
        }
    }

    @Override
    public RegionType getType() {
        return shapeType;
    }

    @Override
    void setName(String name) {
        this.name = name;
    }

    @Override
    public HashMap<String, Object> saveData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("world", getWorld().getName());
        HashMap<String, Object> shapeData = ShapeFactory.serialize(getShape());
        map.put("shape", shapeData);
        return map;
    }

    @Override
    protected void loadData(@Nonnull ConfigurationSection sec) {
        checkNotNull(sec);
        Region shape = ShapeFactory.deserialize(RegionType.valueOf(sec.getParent().getString("type")), sec);
        shape.setWorld(new BukkitWorld(Bukkit.getWorld(sec.getString("world"))));
        setShape(shape);
    }

    @Override
    public boolean contains(@Nonnull Location loc) {
        checkNotNull(loc);
        if (shape == null) {
            return false;
        }
        Block l = loc.getBlock();
        return getWorld() == loc.getWorld() && shape.contains(BlockVector3.at(l.getX(), l.getY(), l.getZ()));
    }
    
    @Override
    public boolean contains(@Nullable World world, float x, float y, float z) {
        return getWorld() == getWorld() && contains(x,y,z);
    }
    
    @Override
    public boolean contains(float x, float y, float z) {
        return hasShape() && shape.contains(BlockVector3.at(x, y, z));
    }
    
    public int getWidth() {
        return shape.getWidth();
    }

    public int getHeight() {
        return shape.getHeight();
    }
    
    public int getLength() {
        return shape.getLength();
    }
    
    @Override
    public BlockVector3 getMin() {
        if (!hasShape()) {
            return null;
        }
        return shape.getMinimumPoint();
    }
    
    @Override
    public BlockVector3 getMax() {
        if (!hasShape()) {
            return null;
        }
        return shape.getMaximumPoint();
    }
    
    @Override
    public RegionSelector createRegionSelector() {
        if (!hasShape()) {
            return null;
        }
        switch (getType()) {
            case CUBOID: {
                return new com.sk89q.worldedit.regions.selector.CuboidRegionSelector(
                        new BukkitWorld(getWorld()),
                        shape.getMinimumPoint(),
                        shape.getMaximumPoint()
                );
            }
            case ELLIPSOID: {
                return new com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector(
                        new BukkitWorld(getWorld()),
                        shape.getCenter().toBlockPoint(),
                        ((EllipsoidRegion) shape).getRadius()
                );
            }
            case POLYGONAL: {
                return new com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector(
                        new BukkitWorld(getWorld()),
                        ((Polygonal2DRegion) shape).getPoints(),
                        ((Polygonal2DRegion) shape).getMinimumY(),
                        ((Polygonal2DRegion) shape).getMaximumY()
                );
            }
            case CYLINDER: {
                return new com.sk89q.worldedit.regions.selector.CylinderRegionSelector(
                        new BukkitWorld(getWorld()),
                        shape.getCenter().toVector2().toBlockPoint(),
                        ((CylinderRegion) shape).getRadius(),
                        ((CylinderRegion) shape).getMinimumY(),
                        ((CylinderRegion) shape).getMaximumY()
                );
            }
            case CONVEX: {
                com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector sel = new com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector(new BukkitWorld(getWorld()));
                for (BlockVector3 vertex : ((ConvexPolyhedralRegion) shape).getVertices()) {
                    ((ConvexPolyhedralRegion) sel.getIncompleteRegion()).addVertex(vertex);
                }
                return sel;
            }
            case BOOLE: {
                return new BooleRegionSelector((BooleRegion) shape);
            }
            default:
                return null;
        }
    }

    @Override
    public World getWorld() {
        return hasShape() ? Bukkit.getWorld(shape.getWorld().getName()) : null;
    }

    
    public Iterator<BlockVector3> iterator() {
        return hasShape() ? shape.iterator() : null;
    }
    
    @Override
    public Location getRelative(int deltaX, int deltaY, int deltaZ) {
        if(!hasShape()) {
            return null;
        }
        BlockVector3 v = shape.getMinimumPoint();
        return new Location(
                    getWorld(),
                v.getX() + deltaX,
                v.getY() + deltaY,
                v.getZ() + deltaZ
        );
    }
}
