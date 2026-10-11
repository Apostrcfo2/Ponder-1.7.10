package net.createmod.ponder1710.foundation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

// Invisible dummy entity used purely as a camera anchor inside a PonderLevel (SubWorldClient).
// Never added to the real world, never affects real chunk loading.
public class PonderCameraEntity extends EntityLivingBase {

    public PonderCameraEntity(World world) {
        super(world);
        this.noClip = true;
        this.setSize(0, 0);
        this.setInvisible(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }
    public void readEntityFromNBT(NBTTagCompound tag) {}
    public void writeEntityToNBT(NBTTagCompound tag) {}

    @Override
    public boolean canBePushed() { return false; }

    @Override
    public boolean isEntityInvulnerable() { return true; }

    @Override
    public net.minecraft.item.ItemStack[] getLastActiveItems() { return new net.minecraft.item.ItemStack[4]; }

    @Override
    public void setCurrentItemOrArmor(int slot, net.minecraft.item.ItemStack stack) {}

    @Override
    public net.minecraft.item.ItemStack getHeldItem() { return null; }

    @Override
    public net.minecraft.item.ItemStack getEquipmentInSlot(int slot) { return null; }
}
