package net.createmod.metanip.levelWrappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

// 1.7.10 port of SchematicLevelAccessor
// BlockPos -> int[] {x,y,z}, BlockState -> Block+meta, BoundingBox -> int[] bounds
public interface SchematicLevelAccessor {

    Set<Long> getAllPositions();

    List<Entity> getEntityList();

    Map<Long, Object[]> getBlockMap(); // Object[] = {Block, int meta}

    int[] getBounds(); // {minX,minY,minZ,maxX,maxY,maxZ}

    void setBounds(int[] bounds);

    Iterable<TileEntity> getTileEntitiesIterable();

    Iterable<TileEntity> getRenderedTileEntities();
}
