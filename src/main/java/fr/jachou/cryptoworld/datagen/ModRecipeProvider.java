package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    private static final List<ItemLike> CRYPTONIUM_ORES = List.of(ModBlocks.CRYPTONIUM_ORE.get().asItem());
    private static final List<ItemLike> SILIICUM_ORES = List.of(ModBlocks.SILICIUM_ORE.get().asItem());

    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, SILIICUM_ORES, RecipeCategory.MISC, ModItems.SILICIUM.get(), 0.7f, 200, "cryptonium", "_smelting");
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, SILIICUM_ORES, RecipeCategory.MISC, ModItems.SILICIUM.get(), 0.7f, 100, "cryptonium", "_blasting");

        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, CRYPTONIUM_ORES, RecipeCategory.MISC, ModItems.CRYPTONIUM_INGOT.get(), 0.7f, 100, "cryptonium", "_blasting");
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, CRYPTONIUM_ORES, RecipeCategory.MISC, ModItems.CRYPTONIUM_INGOT.get(), 0.7f, 200, "cryptonium", "_smelting");

        // Silicium Group
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SILICIUM_HYPERCHARGED.get())
                .pattern("SRS")
                .pattern("RQR")
                .pattern("SRS")
                .define('S', ModItems.SILICIUM.get())
                .define('R', Items.REDSTONE)
                .define('Q', Items.QUARTZ)
                .unlockedBy(getHasName(ModItems.SILICIUM.get()), has(ModItems.SILICIUM.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SILICIUM.get(), 9)
                        .requires(ModBlocks.SILICIUM_BLOCK.get())
                        .unlockedBy(getHasName(ModBlocks.SILICIUM_BLOCK.get()), has(ModBlocks.SILICIUM_BLOCK.get()))
                        .save(recipeOutput);

        // Cryptonium Group
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CRYPTONIUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.CRYPTONIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.CRYPTONIUM_INGOT.get()), has(ModItems.CRYPTONIUM_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CRYPTONIUM_INGOT.get(), 9)
                .requires(ModBlocks.CRYPTONIUM_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.CRYPTONIUM_BLOCK.get()), has(ModBlocks.CRYPTONIUM_BLOCK.get()))
                .save(recipeOutput);


    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer,
                                                                       AbstractCookingRecipe.Factory<T> factory, List<ItemLike> pIngredients,
                                                                       RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                            pExperience, pCookingTime, pCookingSerializer, factory)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, CryptoWorld.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    /*
    oreCooking(......, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, .........);
     */
}
