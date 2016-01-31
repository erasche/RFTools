package mcjty.rftools.blocks.spaceprojector;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public class SpaceChamberControllerTileEntity extends GenericTileEntity {
    private BlockPos minCorner;
    private BlockPos maxCorner;
    private int channel = -1;

    public BlockPos getMinCorner() {
        return minCorner;
    }

    public BlockPos getMaxCorner() {
        return maxCorner;
    }

    public void createChamber(EntityPlayer player) {
        int x1 = getPos().getX();
        int y1 = getPos().getY();
        int z1 = getPos().getZ();
        int x2 = x1;
        int y2 = y1;
        int z2 = z1;
        for (int i = 1 ; i < SpaceProjectorConfiguration.maxSpaceChamberDimension; i++) {
            if (x2 == x1) {
                if (worldObj.getBlockState(new BlockPos(x1 - i, y1, z1)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                    x2 = x1-i;
                } else if (worldObj.getBlockState(new BlockPos(x1 + i, y1, z1)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                    x2 = x1+i;
                }
            }
            if (z2 == z1) {
                if (worldObj.getBlockState(new BlockPos(x1, y1, z1 - i)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                    z2 = z1-i;
                } else if (worldObj.getBlockState(new BlockPos(x1, y1, z1 + i)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                    z2 = z1+i;
                }
            }
        }

        if (x1 == x2 || z2 == z1) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        if (worldObj.getBlockState(new BlockPos(x2, y1, z2)).getBlock() != SpaceProjectorSetup.spaceChamberBlock) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        for (int i = 1 ; i < SpaceProjectorConfiguration.maxSpaceChamberDimension; i++) {
            if (worldObj.getBlockState(new BlockPos(x1, y1 - i, z1)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                y2 = y1-i;
                break;
            }
            if (worldObj.getBlockState(new BlockPos(x1, y1 + i, z1)).getBlock() == SpaceProjectorSetup.spaceChamberBlock) {
                y2 = y1+i;
                break;
            }
        }

        if (y1 == y2) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        if (worldObj.getBlockState(new BlockPos(x2, y2, z2)).getBlock() != SpaceProjectorSetup.spaceChamberBlock) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        if (worldObj.getBlockState(new BlockPos(x1, y2, z2)).getBlock() != SpaceProjectorSetup.spaceChamberBlock) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        if (worldObj.getBlockState(new BlockPos(x2, y2, z1)).getBlock() != SpaceProjectorSetup.spaceChamberBlock) {
            Logging.message(player, EnumChatFormatting.RED + "Not a valid chamber shape!");
            return;
        }

        // We have a valid shape.
        minCorner = new BlockPos(Math.min(x1, x2)+1, Math.min(y1, y2)+1, Math.min(z1, z2)+1);
        maxCorner = new BlockPos(Math.max(x1, x2)-1, Math.max(y1, y2)-1, Math.max(z1, z2)-1);
        if (minCorner.getX() > maxCorner.getX() || minCorner.getY() > maxCorner.getY() || minCorner.getZ() > maxCorner.getZ()) {
            Logging.message(player, EnumChatFormatting.RED + "Chamber is too small!");
            minCorner = null;
            maxCorner = null;
            return;
        }

        Logging.message(player, EnumChatFormatting.WHITE + "Chamber succesfully created!");

        SpaceChamberRepository chamberRepository = SpaceChamberRepository.getChannels(worldObj);
        SpaceChamberRepository.SpaceChamberChannel chamberChannel = chamberRepository.getOrCreateChannel(channel);
        chamberChannel.setDimension(worldObj.provider.getDimensionId());
        chamberChannel.setMinCorner(minCorner);
        chamberChannel.setMaxCorner(maxCorner);
        chamberRepository.save(worldObj);

        markDirtyClient();
    }

    public int getChannel() {
        return channel;
    }

    public int getChamberSize() {
        if (channel == -1) {
            return -1;
        }
        if (minCorner == null) {
            return -1;
        }
        return BlockPosTools.area(minCorner, maxCorner);
    }

    public void setChannel(int channel) {
        this.channel = channel;
        markDirtyClient();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        minCorner = BlockPosTools.readFromNBT(tagCompound, "minCorner");
        maxCorner = BlockPosTools.readFromNBT(tagCompound, "maxCorner");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        channel = tagCompound.getInteger("channel");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        BlockPosTools.writeToNBT(tagCompound, "minCorner", minCorner);
        BlockPosTools.writeToNBT(tagCompound, "maxCorner", maxCorner);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("channel", channel);
    }
}
