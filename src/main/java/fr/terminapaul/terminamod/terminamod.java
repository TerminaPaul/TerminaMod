package fr.terminapaul.terminamod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
//import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// Le nom du mod doit correspondre à l'entrée dans le fichier META-INF/mods.toml
@Mod(terminamod.MODID)
public class terminamod {
    // Définir l'ID du mod dans un endroit commun pour que tout le monde puisse y accéder
    public static final String MODID = "terminamod";
    // Référence directe à un logger slf4j
    private static final Logger LOGGER = LogUtils.getLogger();
    // Crée un Deferred Register pour les Blocs qui seront enregistrés sous le namespace "terminamod"
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Crée un Deferred Register pour les Items qui seront enregistrés sous le namespace "terminamod"
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Crée un Deferred Register pour les CreativeModeTabs sous le namespace "terminamod"
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Crée un nouveau Bloc avec l'ID "terminamod : example_block", combinant le namespace et le chemin
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .setId(BLOCKS.key("example_block"))
                    .mapColor(MapColor.STONE)
            )
    );


    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Crée un nouveau BlockItem avec l'ID "terminamod : example_block_item", combinant le namespace et le chemin
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
            () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().setId(ITEMS.key("example_block")))
    );

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Crée un nouvel item alimentaire avec l'ID "terminamod : example_item"
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new Item(new Item.Properties().setId(ITEMS.key("example_item"))
            )
    );

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Crée un onglet créatif avec l'ID "terminamod : example_tab" pour l'élément example_item, placé après l'onglet combat
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                output.accept(EXAMPLE_BLOCK.get());
                output.accept(EXAMPLE_BLOCK_ITEM.get());
                // Ajouter l'élément example_item à l'onglet
            }).build());

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public terminamod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Enregistrer la méthode commonSetup pour le modloading
        modEventBus.addListener(this::commonSetup);

        // Enregistrer le Deferred Register auprès du mod event bus pour que les blocs soient enregistrés
        BLOCKS.register(modEventBus);
        // Enregistrer le Deferred Register auprès du mod event bus pour que les items soient enregistrés
        ITEMS.register(modEventBus);
        // Enregistrer le Deferred Register auprès du mod event bus pour que les onglets créatifs soient enregistrés
        CREATIVE_MODE_TABS.register(modEventBus);

        // Enregistrer le mod pour les événements serveur et autres événements du jeu
        MinecraftForge.EVENT_BUS.register(this);

        // Enregistrer l'item dans un onglet créatif
        modEventBus.addListener(this::addCreative);

        // Enregistrer la spécification de configuration pour que Forge puisse créer et charger le fichier de configuration
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Code de configuration commune
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Faites quelque chose lorsque le serveur démarre
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Code de configuration côté client
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}