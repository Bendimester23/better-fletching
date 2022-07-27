package hu.bendi.betterarchery.block;

import hu.bendi.betterarchery.ArcheryMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    //BEs
    public static BlockEntityType<FletchingTableBlockEntity> FLETCHING_TABLE_BLOCK_ENTITY;

    public static void registerBlocks() {
        //TODO add blocks
    }

    public static void registerBlockEntities() {
        FLETCHING_TABLE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, ArcheryMod.i("fletching_table_block_entity"), FabricBlockEntityTypeBuilder.create(FletchingTableBlockEntity::new, Blocks.FLETCHING_TABLE).build());
    }
}
