package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CryptoWorld.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Cryptocurrency
        simpleItem(ModItems.BITCOIN);
        simpleItem(ModItems.ETHEREUM);

        // Ores & Ingots
        simpleItem(ModItems.CRYPTONIUM_INGOT);
        simpleItem(ModItems.SILICIUM);
        simpleItem(ModItems.SILICIUM_HYPERCHARGED);
        simpleItem(ModItems.CRYPTOWORLD_DETECTOR);

        // Computers
        simpleItem(ModItems.RAM);
        simpleItem(ModItems.GTX_1080);
        simpleItem(ModItems.RTX_2080);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CryptoWorld.MODID,"item/" + item.getId().getPath()));
    }
}
