package scp002.mod.dropoff.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DropOffConfig {

  final String categoryGeneral = "general";


  public static ForgeConfigSpec.BooleanValue dropOff;
  public static ForgeConfigSpec.BooleanValue dropOffOnlyFullStacks;

  public static ForgeConfigSpec.BooleanValue sortPlayerInventory;


  public static ForgeConfigSpec.IntValue scanRadius;

  public static ForgeConfigSpec.ConfigValue<String> excludeItemsWithNames;

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

    dropOffOnlyFullStacks = builder.comment("Move only full item stacks from the player inventory.").define("DropOff only full stacks", DefaultValues.dropOffOnlyFullStacks);

    scanRadius = builder.comment("Radius in blocks to check containers around the player.").
            defineInRange("Scan radius", DefaultValues.scanRadius,0,Integer.MAX_VALUE);


    builder.pop();

    // -------------------------------------------------Containers--------------------------------------------------

  }

  private abstract class DefaultValues {

    private static final boolean displayMessage = true;
    private static final boolean dropOff = true;
    private static final boolean dropOffOnlyFullStacks = false;
    private static final boolean highlightContainers = true;
    private static final boolean showInventoryButton = true;

    private static final int creativeInventoryButtonXOffset = 71;
    private static final int creativeInventoryButtonYOffset = -63;
    private static final int highlightDelay = 3000;
    private static final int scanRadius = 6;
    private static final int survivalInventoryButtonXOffset = 7;
    private static final int survivalInventoryButtonYOffset = -14;
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
              DefaultValues.creativeInventoryButtonXOffset,Integer.MIN_VALUE,Integer.MAX_VALUE);

      creativeInventoryButtonYOffset = builder.comment("[Client-side] Creative inventory button position height offset.").defineInRange("Creative inventory button Y offset",
              DefaultValues.creativeInventoryButtonYOffset,Integer.MIN_VALUE,Integer.MAX_VALUE);

      highlightDelay = builder.comment("[Client-side] Blocks highlighting delay in milliseconds. Delay < 0 means forever.").defineInRange("Highlight delay", DefaultValues.highlightDelay,-1,Integer.MAX_VALUE);

      survivalInventoryButtonXOffset = builder.comment("[Client-side] Survival inventory button position width offset.").defineInRange("Survival inventory button X offset",
              DefaultValues.survivalInventoryButtonXOffset,Integer.MIN_VALUE,Integer.MAX_VALUE);

      survivalInventoryButtonYOffset = builder.comment("[Client-side] Survival inventory button position height offset.").defineInRange("Survival inventory button Y offset",
              DefaultValues.survivalInventoryButtonYOffset,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }
  }
}