package com.vincentmet.customquests.tileentity;

import com.vincentmet.customquests.ItemStackHandlerCapability;
import javax.annotation.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;

public class DeliveryBlockTileEntity extends TileEntity{
	private PlayerEntity currentSubmitter;
	
	private final ItemStackHandlerCapability itemHandler = new ItemStackHandlerCapability(null, this);;
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);
	
	public DeliveryBlockTileEntity(){
		super(null/*Objects.TileEntities.DELIVERY_BLOCK*/);
	}
	
	public void setCurrentSubmitter(PlayerEntity currentSubmitter){
		this.currentSubmitter = currentSubmitter;
		itemHandler.setPlayer(currentSubmitter);
	}
	
	public ItemStackHandlerCapability getItemHandler(){
		return itemHandler;
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(side == Direction.UP){
			if(cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)){
				if(currentSubmitter!=null)return handler.cast();
			}
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound){
		if(currentSubmitter!=null){
			compound.putUniqueId("currentSubmitter", currentSubmitter.getUniqueID());
		}
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound){
		super.read(state, compound);
		if(compound.contains("currentSubmitter")){
			//setCurrentSubmitter(compound.getUniqueId("currentSubmitter"));//fixme DELIVERYBLOCK
		}
	}
}
