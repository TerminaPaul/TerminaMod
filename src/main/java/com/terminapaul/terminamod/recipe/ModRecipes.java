package com.terminapaul.terminamod.recipe;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TerminaMod.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, TerminaMod.MOD_ID);

    public static final RegistryObject<RecipeType<IndustrialSmelterRecipe>> INDUSTRIAL_SMELTER_TYPE =
            TYPES.register(IndustrialSmelterRecipe.ID, () -> RecipeType.simple(
                    new net.minecraft.resources.ResourceLocation(TerminaMod.MOD_ID, IndustrialSmelterRecipe.ID)));

    public static final RegistryObject<RecipeSerializer<IndustrialSmelterRecipe>> INDUSTRIAL_SMELTER_SERIALIZER =
            SERIALIZERS.register(IndustrialSmelterRecipe.ID, IndustrialSmelterRecipe.Serializer::new);
}