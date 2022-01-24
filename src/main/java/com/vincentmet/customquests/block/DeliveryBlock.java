package com.vincentmet.customquests.block;

import com.vincentmet.customquests.gui.DeliveryScreen;
import com.vincentmet.customquests.tileentity.DeliveryBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DeliveryBlock extends ContainerBlock{
	public DeliveryBlock(){
		super(Block.Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).strength(2F).noCollission());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(!world.isClientSide()){
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof DeliveryBlockTileEntity){
				if(player.getItemInHand(hand).getItem() == Items.CARROT_ON_A_STICK){
					((DeliveryBlockTileEntity)te).setCurrentSubmitter(player);
					LOGGER.info("Setting player to: " + player.getDisplayName().getString());
				}else{
					Minecraft.getInstance().setScreen(new DeliveryScreen(pos));
				}
				return ActionResultType.PASS;
			}
		}
		return ActionResultType.FAIL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader world){
		return new DeliveryBlockTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
}
