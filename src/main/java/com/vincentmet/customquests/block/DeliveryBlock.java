package com.vincentmet.customquests.block;

import com.vincentmet.customquests.gui.DeliveryScreen;
import com.vincentmet.customquests.tileentity.DeliveryBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class DeliveryBlock extends BaseEntityBlock {
	public DeliveryBlock(){
		super(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).strength(2F).noCollission());
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(!world.isClientSide()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof DeliveryBlockTileEntity){
				if(player.getItemInHand(hand).getItem() == Items.CARROT_ON_A_STICK){
					((DeliveryBlockTileEntity)te).setCurrentSubmitter(player);
					LOGGER.info("Setting player to: " + player.getDisplayName().getString());
				}else{
					Minecraft.getInstance().setScreen(new DeliveryScreen(pos));
				}
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new DeliveryBlockTileEntity(blockPos, blockState);
	}
}
