package eyeq.clock;

import eyeq.clock.block.BlockClock;
import eyeq.util.client.model.UModelLoader;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.io.File;

import static eyeq.clock.Clock.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
@Mod.EventBusSubscriber
public class Clock {
    public static final String MOD_ID = "eyeq_clock";

    @Mod.Instance(MOD_ID)
    public static Clock instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static Block clock;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        addRecipes();
        if(event.getSide().isServer()) {
            return;
        }
        renderItemModels();
        createFiles();
    }

    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event) {
        clock = new BlockClock(Material.ROCK).setHardness(1.0F).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.DECORATIONS);

        GameRegistry.register(clock, resource.createResourceLocation("clock"));
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
        GameRegistry.register(new ItemBlock(clock), clock.getRegistryName());
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(clock), "X X", "YCY", "YYY",
                'X', UOreDictionary.OREDICT_GOLD_INGOT, 'Y', UOreDictionary.OREDICT_STONE, 'C', Items.CLOCK));
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemModels() {
        UModelLoader.setCustomModelResourceLocation(clock);
    }

    public static void createFiles() {
        File project = new File("../1.11.2-Clock");

        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, clock, "Clock");
        language.register(LanguageResourceManager.JA_JP, clock, "時計");

        ULanguageCreator.createLanguage(project, MOD_ID, language);
    }
}
