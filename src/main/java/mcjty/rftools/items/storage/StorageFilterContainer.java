package mcjty.rftools.items.storage;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;

public class StorageFilterContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_FILTER = 0;
    public static final int FILTER_SLOTS = 6*3;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_GHOST), CONTAINER_INVENTORY, SLOT_FILTER, 10, 9, 6, 18, 3, 18);
            layoutPlayerInventorySlots(10, 70);
        }
    };

    public StorageFilterContainer(EntityPlayer player) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, new StorageFilterInventory(player));
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
