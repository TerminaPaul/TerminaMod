package com.terminapaul.terminamod;

import com.terminapaul.terminamod.screen.IndustrialSmelterMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TerminaMod.MOD_ID);

    public static final RegistryObject<MenuType<IndustrialSmelterMenu>> INDUSTRIAL_SMELTER =
            MENUS.register("industrial_smelter",
                    () -> IForgeMenuType.create(IndustrialSmelterMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}