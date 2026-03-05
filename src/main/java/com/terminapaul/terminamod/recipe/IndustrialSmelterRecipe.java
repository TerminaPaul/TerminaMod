package com.terminapaul.terminamod.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class IndustrialSmelterRecipe implements Recipe<Container> {

    public static final String ID = "industrial_smelting";

    private final ResourceLocation id;
    private final ItemStack input;
    private final int inputCount;
    private final ItemStack output;
    private final int color;

    public IndustrialSmelterRecipe(ResourceLocation id, ItemStack input, int inputCount,
                                   ItemStack output, int color) {
        this.id = id;
        this.input = input;
        this.inputCount = inputCount;
        this.output = output;
        this.color = color;
    }

    public ItemStack getInputItem() { return input; }
    public int getInputCount() { return inputCount; }
    public int getColor() { return color; }
    public ItemStack getResultItem() { return output.copy(); }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack stack = container.getItem(0);
        return stack.is(input.getItem()) && stack.getCount() >= inputCount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return true; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.INDUSTRIAL_SMELTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INDUSTRIAL_SMELTER_TYPE.get();
    }

    // --- Serializer ---
    public static class Serializer implements RecipeSerializer<IndustrialSmelterRecipe> {

        @Override
        public IndustrialSmelterRecipe fromJson(ResourceLocation id, JsonObject json) {
            ResourceLocation inputId = new ResourceLocation(GsonHelper.getAsString(json, "input"));
            int inputCount = GsonHelper.getAsInt(json, "input_count", 9);

            ResourceLocation outputId = new ResourceLocation(GsonHelper.getAsString(json, "output"));
            int outputCount = GsonHelper.getAsInt(json, "output_count", 1);

            String colorStr = GsonHelper.getAsString(json, "color", "#FFFFFF");
            int color = (int) Long.parseLong(colorStr.replace("#", ""), 16);

            ItemStack inputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inputId));
            ItemStack outputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(outputId), outputCount);

            return new IndustrialSmelterRecipe(id, inputStack, inputCount, outputStack, color);
        }

        @Override
        public IndustrialSmelterRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ItemStack input = buf.readItem();
            int inputCount = buf.readInt();
            ItemStack output = buf.readItem();
            int color = buf.readInt();
            return new IndustrialSmelterRecipe(id, input, inputCount, output, color);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, IndustrialSmelterRecipe recipe) {
            buf.writeItem(recipe.input);
            buf.writeInt(recipe.inputCount);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.color);
        }
    }
}