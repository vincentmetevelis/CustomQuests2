package com.vincentmet.customquests.tileentity;

import com.vincentmet.customquests.ItemStackHandlerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeliveryBlockTileEntity extends BlockEntity {
	private Player currentSubmitter;
	
	private final ItemStackHandlerCapability itemHandler = new ItemStackHandlerCapability(null, this);;
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);
	
	public DeliveryBlockTileEntity(BlockPos pos, BlockState state){
		super(null/*Objects.TileEntities.DELIVERY_BLOCK*/, pos, state);
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
	public void saveAdditional(CompoundTag compound){
		if(currentSubmitter!=null){
			compound.putUUID("currentSubmitter", currentSubmitter.getUUID());
		}
	}
	
	@Override
	public void load(CompoundTag compound){
		super.load(compound);
		if(compound.contains("currentSubmitter")){
			//setCurrentSubmitter(compound.getUniqueId("currentSubmitter"));//fixme DELIVERYBLOCK
		}
	}
}
