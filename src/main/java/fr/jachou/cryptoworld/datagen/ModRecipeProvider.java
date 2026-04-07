package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> CRYPTONIUM_ORES = List.of(ModBlocks.CRYPTONIUM_ORE.get());
    private static final List<ItemLike> SILICIUM_ORES = List.of(ModBlocks.SILICIUM_ORE.get());

    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, SILICIUM_ORES,
                RecipeCategory.MISC, ModItems.SILICIUM.get(), 0.7F, 200, "silicium", "_smelting");
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, SILICIUM_ORES,
                RecipeCategory.MISC, ModItems.SILICIUM.get(), 0.7F, 100, "silicium", "_blasting");
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, CRYPTONIUM_ORES,
                RecipeCategory.MISC, ModItems.CRYPTONIUM_INGOT.get(), 0.9F, 200, "cryptonium", "_smelting");
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, CRYPTONIUM_ORES,
                RecipeCategory.MISC, ModItems.CRYPTONIUM_INGOT.get(), 0.9F, 100, "cryptonium", "_blasting");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SILICIUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.SILICIUM.get())
                .unlockedBy(getHasName(ModItems.SILICIUM.get()), has(ModItems.SILICIUM.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SILICIUM.get(), 9)
                .requires(ModBlocks.SILICIUM_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.SILICIUM_BLOCK.get()), has(ModBlocks.SILICIUM_BLOCK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYPTONIUM_BLOCK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RAM.get())
                .pattern("SRS")
                .pattern("SCS")
                .pattern("IRI")
                .define('S', ModItems.SILICIUM.get())
                .define('R', Items.REDSTONE)
                .define('C', ModItems.CRYPTONIUM_INGOT.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.CRYPTONIUM_INGOT.get()), has(ModItems.CRYPTONIUM_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GTX_1080.get())
                .pattern("SRS")
                .pattern("CGC")
                .pattern("SIS")
                .define('S', ModItems.SILICIUM.get())
                .define('R', Items.REDSTONE)
                .define('C', ModItems.CRYPTONIUM_INGOT.get())
                .define('G', Items.GLASS_PANE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.CRYPTONIUM_INGOT.get()), has(ModItems.CRYPTONIUM_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SILICIUM_HYPERCHARGED.get())
                .pattern("SRS")
                .pattern("RQR")
                .pattern("SRS")
                .define('S', ModItems.SILICIUM.get())
                .define('R', Items.REDSTONE)
                .define('Q', Items.QUARTZ)
                .unlockedBy(getHasName(ModItems.SILICIUM.get()), has(ModItems.SILICIUM.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CRYPTOWORLD_DETECTOR.get())
                .pattern(" S ")
                .pattern("RCR")
                .pattern(" I ")
                .define('S', ModItems.SILICIUM.get())
                .define('R', Items.REDSTONE)
                .define('C', Items.COMPASS)
                .define('I', ModItems.CRYPTONIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.CRYPTONIUM_INGOT.get()), has(ModItems.CRYPTONIUM_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ASIC_MK1.get())
                .pattern("CBC")
                .pattern("RGR")
                .pattern("SIS")
                .define('C', ModItems.CRYPTONIUM_INGOT.get())
                .define('B', ModItems.BITCOIN.get())
                .define('R', Items.REDSTONE)
                .define('G', Items.GOLD_INGOT)
                .define('S', ModItems.SILICIUM.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.BITCOIN.get()), has(ModItems.BITCOIN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PSU_BASIC.get())
                .pattern("ICI")
                .pattern("RQR")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .define('Q', Items.QUARTZ)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PSU_REDLINE.get())
                .pattern("GDG")
                .pattern("PQP")
                .pattern("GRG")
                .define('G', Items.GOLD_INGOT)
                .define('D', Items.DIAMOND)
                .define('P', ModItems.PSU_BASIC.get())
                .define('Q', Items.QUARTZ)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(getHasName(ModItems.PSU_BASIC.get()), has(ModItems.PSU_BASIC.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AIR_COOLER.get())
                .pattern("ICI")
                .pattern("RFR")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .define('F', Items.GLASS_PANE)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WATER_COOLER.get())
                .pattern("BQB")
                .pattern("CAC")
                .pattern("BQB")
                .define('B', Items.WATER_BUCKET)
                .define('Q', Items.QUARTZ)
                .define('C', Items.COPPER_INGOT)
                .define('A', ModItems.AIR_COOLER.get())
                .unlockedBy(getHasName(ModItems.AIR_COOLER.get()), has(ModItems.AIR_COOLER.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRID_UPGRADE.get())
                .pattern("ERE")
                .pattern("CGC")
                .pattern("ERE")
                .define('E', Items.EMERALD)
                .define('R', Items.REDSTONE)
                .define('C', ModItems.CRYPTONIUM_INGOT.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy(getHasName(Items.EMERALD), has(Items.EMERALD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.MINER_BLOCK.get())
                .pattern("PGA")
                .pattern("RMR")
                .pattern("ICI")
                .define('P', ModItems.PSU_BASIC.get())
                .define('G', ModItems.GTX_1080.get())
                .define('A', ModItems.AIR_COOLER.get())
                .define('R', ModItems.RAM.get())
                .define('M', ModItems.SILICIUM_HYPERCHARGED.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(ModItems.PSU_BASIC.get()), has(ModItems.PSU_BASIC.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SERVER_BLOCK.get())
                .pattern("IGI")
                .pattern("RBR")
                .pattern("UGU")
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .define('B', ModItems.BITCOIN.get())
                .define('U', ModItems.GRID_UPGRADE.get())
                .unlockedBy(getHasName(ModItems.BITCOIN.get()), has(ModItems.BITCOIN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RTX_2080.get())
                .pattern("BDB")
                .pattern("CGC")
                .pattern("SXS")
                .define('B', ModItems.BITCOIN.get())
                .define('D', Items.DIAMOND)
                .define('C', ModItems.CRYPTONIUM_INGOT.get())
                .define('G', ModItems.GTX_1080.get())
                .define('S', ModItems.SILICIUM_HYPERCHARGED.get())
                .define('X', Items.EMERALD_BLOCK)
                .unlockedBy(getHasName(ModItems.BITCOIN.get()), has(ModItems.BITCOIN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SOLAR_PANEL.get())
                .pattern("GGG")
                .pattern("SCS")
                .pattern("IRI")
                .define('G', Items.GLASS_PANE)
                .define('S', ModItems.SILICIUM.get())
                .define('C', Items.COPPER_INGOT)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(ModItems.SILICIUM.get()), has(ModItems.SILICIUM.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WIND_TURBINE.get())
                .pattern(" I ")
                .pattern("CQC")
                .pattern(" R ")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('Q', Items.QUARTZ)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BATTERY_BANK.get())
                .pattern("SRS")
                .pattern("CBC")
                .pattern("SIS")
                .define('S', ModItems.SILICIUM_HYPERCHARGED.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('C', Items.COPPER_INGOT)
                .define('B', Items.IRON_BLOCK)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.SILICIUM_HYPERCHARGED.get()), has(ModItems.SILICIUM_HYPERCHARGED.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.VENTILATION_UNIT.get())
                .pattern("III")
                .pattern("CAC")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('A', ModItems.AIR_COOLER.get())
                .unlockedBy(getHasName(ModItems.AIR_COOLER.get()), has(ModItems.AIR_COOLER.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WATER_COOLING_UNIT.get())
                .pattern("WBW")
                .pattern("QCQ")
                .pattern("WBW")
                .define('W', Items.WATER_BUCKET)
                .define('B', ModBlocks.BATTERY_BANK.get())
                .define('Q', Items.QUARTZ)
                .define('C', ModItems.WATER_COOLER.get())
                .unlockedBy(getHasName(ModItems.WATER_COOLER.get()), has(ModItems.WATER_COOLER.get()))
                .save(recipeOutput);
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput,
                                                                       RecipeSerializer<T> serializer,
                                                                       AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> ingredients,
                                                                       RecipeCategory category,
                                                                       ItemLike result,
                                                                       float experience,
                                                                       int cookingTime,
                                                                       String group,
                                                                       String recipeName) {
        for (ItemLike itemLike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemLike), category, result, experience, cookingTime, serializer, factory)
                    .group(group)
                    .unlockedBy(getHasName(itemLike), has(itemLike))
                    .save(recipeOutput, CryptoWorld.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemLike));
        }
    }
}
