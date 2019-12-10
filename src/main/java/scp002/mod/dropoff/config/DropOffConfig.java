package scp002.mod.dropoff.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DropOffConfig {

  public static final String delimiter = DefaultValues.delimiter;

  final String categoryContainers = "containers";
  final String categoryGeneral = "general";

  private final List<String> propertyOrder = new ArrayList<>();
  private final Map<String, Set<String>> categoryToPropertyKeySet = new HashMap<>();

  public static ForgeConfigSpec.BooleanValue checkBeacons;
  public static ForgeConfigSpec.BooleanValue checkBrewingStands;
  public static ForgeConfigSpec.BooleanValue checkChests;
  public static ForgeConfigSpec.BooleanValue checkDispensers;
  public static ForgeConfigSpec.BooleanValue checkDroppers;
  public static ForgeConfigSpec.BooleanValue checkEnderChests;
  public static ForgeConfigSpec.BooleanValue checkFurnaces;
  public static ForgeConfigSpec.BooleanValue checkHoppers;
  public static ForgeConfigSpec.BooleanValue checkShulkerBoxes;
  public static ForgeConfigSpec.BooleanValue dropOff;
  public static ForgeConfigSpec.BooleanValue dropOffEveryPlace;
  public static ForgeConfigSpec.BooleanValue dropOffOnlyFullStacks;

  public static ForgeConfigSpec.BooleanValue sortContainers;
  public static ForgeConfigSpec.BooleanValue sortPlayerInventory;


  public static ForgeConfigSpec.IntValue scanRadius;

  public static ForgeConfigSpec.ConfigValue<String> excludeItemsWithNames;
  public static ForgeConfigSpec.ConfigValue<String> processContainersWithNames;
  public static ForgeConfigSpec.ConfigValue<String> sortContainersWithNames;

  public static final Client CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  public static final DropOffConfig SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
    final Pair<DropOffConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(DropOffConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }

  public DropOffConfig(ForgeConfigSpec.Builder builder) {

    // propertyOrder.clear();
    // categoryToPropertyKeySet.clear()

    // ---------------------------------------------------General---------------------------------------------------

    // Booleans

    builder.push(categoryGeneral);

    dropOff = builder.comment("Move items from the player inventory to the nearby containers.").define("DropOff", DefaultValues.dropOff);

    dropOffEveryPlace = builder.comment("Move items to all containers, even those that are not defined in the configuration.")
            .define("DropOff everyplace", DefaultValues.dropOffEveryPlace);

    dropOffOnlyFullStacks = builder.comment("Move only full item stacks from the player inventory.").define("DropOff only full stacks", DefaultValues.dropOffOnlyFullStacks);
    sortContainers = builder.comment("Sort nearby containers.").define("Sort containers", DefaultValues.sortContainers);
    sortPlayerInventory = builder.comment("Sort player inventory.").define("Sort player inventory", DefaultValues.sortPlayerInventory);

    scanRadius = builder.comment("Radius in blocks to check containers around the player.").
            defineInRange("Scan radius", DefaultValues.scanRadius,0,Integer.MAX_VALUE);

    // Strings
    excludeItemsWithNames = builder.comment("Do not move items with the following names from the player inventory. Delimiter is '" +
            delimiter + "'. Wildcards allowed.").define("Exclude items with names", DefaultValues.excludeItemsWithNames);

    builder.pop();

    // -------------------------------------------------Containers--------------------------------------------------
    builder.push(categoryContainers);
    // Booleans
    checkBeacons = builder.comment("Check nearby beacons.").define("Check beacons", DefaultValues.checkBeacons);

    checkBrewingStands = builder.comment("Check nearby brewing stands.").define("Check brewing stands", DefaultValues.checkBrewingStands);

    checkChests = builder.comment("Check nearby chests.").define("Check chests", DefaultValues.checkChests);

    checkDispensers = builder.comment("Check nearby dispensers.").define("Check dispensers", DefaultValues.checkDispensers);

    checkDroppers = builder.comment("Check nearby droppers.").define("Check droppers", DefaultValues.checkDroppers);

    checkEnderChests = builder.comment("Check nearby ender chests.").define("Check ender chests", DefaultValues.checkEnderChests);

    checkFurnaces = builder.comment("Check nearby furnaces.").define("Check furnaces", DefaultValues.checkFurnaces);

    checkHoppers = builder.comment("Check nearby hoppers.").define("Check hoppers", DefaultValues.checkHoppers);

    checkShulkerBoxes = builder.comment("Check nearby shulker boxes.").define("Check shulker boxes", DefaultValues.checkShulkerBoxes);

    // Strings
    processContainersWithNames = builder.comment("Try to manipulate containers with the following names. Delimiter is '" + delimiter +
            "'. Wildcards allowed.").define("Process containers with names",
            DefaultValues.processContainersWithNames);


    sortContainersWithNames = builder.comment("Sort containers with the following names. Delimiter is '" + delimiter +
            "'. Wildcards allowed.").define("Sort containers with names",
            DefaultValues.sortContainersWithNames);

  }

  private abstract class DefaultValues {

    private static final boolean checkBeacons = true;
    private static final boolean checkBrewingStands = true;
    private static final boolean checkChests = true;
    private static final boolean checkDispensers = true;
    private static final boolean checkDroppers = true;
    private static final boolean checkEnderChests = true;
    private static final boolean checkFurnaces = true;
    private static final boolean checkHoppers = true;
    private static final boolean checkShulkerBoxes = true;
    private static final boolean displayMessage = true;
    private static final boolean dropOff = true;
    private static final boolean dropOffEveryPlace = false;
    private static final boolean dropOffOnlyFullStacks = false;
    private static final boolean highlightContainers = true;
    private static final boolean showInventoryButton = true;
    private static final boolean sortContainers = true;
    private static final boolean sortPlayerInventory = true;

    private static final int creativeInventoryButtonXOffset = 71;
    private static final int creativeInventoryButtonYOffset = -63;
    private static final int highlightDelay = 3000;
    private static final int scanRadius = 4;
    private static final int survivalInventoryButtonXOffset = 7;
    private static final int survivalInventoryButtonYOffset = -14;

    private static final String delimiter = ",";
    private static final String excludeItemsWithNames = "";
    private static final String processContainersWithNames = "*Barrel*" + delimiter + "*Chest*" + delimiter +
            "*Drawer*";
    private static final String sortContainersWithNames = "*Chest*" + delimiter + "*Shulker Box";

  }

  public static class Client {

    public static ForgeConfigSpec.BooleanValue highlightContainers;
    public static ForgeConfigSpec.BooleanValue showInventoryButton;
    public static ForgeConfigSpec.BooleanValue displayMessage;

    public static ForgeConfigSpec.IntValue creativeInventoryButtonXOffset;
    public static ForgeConfigSpec.IntValue creativeInventoryButtonYOffset;

    public static ForgeConfigSpec.IntValue survivalInventoryButtonXOffset;
    public static ForgeConfigSpec.IntValue survivalInventoryButtonYOffset;

    public static ForgeConfigSpec.IntValue highlightDelay;


    public Client(ForgeConfigSpec.Builder builder){
      builder.push("general");
      //booleans
      highlightContainers = builder.comment("[Client-side] Highlight nearby containers.").define("Highlight containers",DefaultValues.highlightContainers);
      displayMessage = builder.comment("[Client-side] Print information to the chat when task is complete.").define("Display Message", DefaultValues.displayMessage);
      showInventoryButton = builder.comment("[Client-side] Show button in the player inventory.").define("Show inventory button", DefaultValues.showInventoryButton);

      // Integers
      creativeInventoryButtonXOffset = builder.comment("[Client-side] Creative inventory button position width offset.").defineInRange("Creative inventory button X offset",
              DefaultValues.creativeInventoryButtonXOffset,0,Integer.MAX_VALUE);

      creativeInventoryButtonYOffset = builder.comment("[Client-side] Creative inventory button position height offset.").defineInRange("Creative inventory button Y offset",
              DefaultValues.creativeInventoryButtonYOffset,1,Integer.MAX_VALUE);

      highlightDelay = builder.comment("[Client-side] Blocks highlighting delay in milliseconds. Delay < 0 means forever.").defineInRange("Highlight delay", DefaultValues.highlightDelay,-1,Integer.MAX_VALUE);

      survivalInventoryButtonXOffset = builder.comment("[Client-side] Survival inventory button position width offset.").defineInRange("Survival inventory button X offset",
              DefaultValues.survivalInventoryButtonXOffset,0,Integer.MAX_VALUE);

      survivalInventoryButtonYOffset = builder.comment("[Client-side] Survival inventory button position height offset.").defineInRange("Survival inventory button Y offset",
              DefaultValues.survivalInventoryButtonYOffset,0,Integer.MAX_VALUE);
    }
  }
}