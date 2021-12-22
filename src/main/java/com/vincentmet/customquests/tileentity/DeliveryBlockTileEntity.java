package com.vincentmet.customquests.tileentity;

import com.vincentmet.customquests.ItemStackHandlerCapability;
import javax.annotation.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;

public class DeliveryBlockTileEntity extends BlockEntity{
	private Player currentSubmitter;
	
	private final ItemStackHandlerCapability itemHandler = new ItemStackHandlerCapability(null, this);;
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);
	
	public DeliveryBlockTileEntity(){
		super(null, null, null/*Objects.TileEntities.DELIVERY_BLOCK*/);
	}
	
	public void setCurrentSubmitter(Player currentSubmitter){
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
	public CompoundTag save(CompoundTag compound){
		if(currentSubmitter!=null){
			compound.putUUID("currentSubmitter", currentSubmitter.getUUID());
		}
		return super.save(compound);
	}
	
	@Override
	public void load(CompoundTag compound){
		super.load(compound);
		if(compound.contains("currentSubmitter")){
			//setCurrentSubmitter(compound.getUniqueId("currentSubmitter"));//fixme DELIVERYBLOCK
		}
	}
}
