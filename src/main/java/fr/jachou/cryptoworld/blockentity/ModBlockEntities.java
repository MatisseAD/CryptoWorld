package fr.jachou.cryptoworld.blockentity;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CryptoWorld.MODID);

    public static final RegistryObject<BlockEntityType<MinerBlockEntity>> MINER_BLOCK_ENTITY = BLOCK_ENTITIES.register("miner_block_entity",
            () -> BlockEntityType.Builder.of(MinerBlockEntity::new, ModBlocks.MINER_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
