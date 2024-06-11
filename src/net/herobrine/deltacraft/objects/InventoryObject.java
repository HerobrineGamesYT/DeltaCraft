package net.herobrine.deltacraft.objects;

public interface InventoryObject {
    // Used for pathing of inventory objects.
    void pickSlot();

    int getCurrentSlot();

    void setCurrentSlot(int slot);

}
