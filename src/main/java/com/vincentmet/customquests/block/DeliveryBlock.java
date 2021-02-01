package com.vincentmet.customquests.block;

import com.vincentmet.customquests.gui.DeliveryScreen;
import com.vincentmet.customquests.tileentity.DeliveryBlockTileEntity;
import javax.annotation.Nullable;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class DeliveryBlock extends ContainerBlock{
	public DeliveryBlock(){
		super(Block.Properties.create(new Material.Builder(MaterialColor.BLACK).build()).hardnessAndResistance(2F).doesNotBlockMovement());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(!world.isRemote){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof DeliveryBlockTileEntity){
				if(player.getHeldItem(hand).getItem() == Items.CARROT_ON_A_STICK){
					((DeliveryBlockTileEntity)te).setCurrentSubmitter(player);
					LOGGER.info("Setting player to: " + player.getDisplayName().getFormattedText());
				}else{
					Minecraft.getInstance().displayGuiScreen(new DeliveryScreen(pos));
				}
			}
		}
		return true;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean doesSideBlockRendering(BlockState state, IEnviromentBlockReader world, BlockPos pos, Direction face){
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader world){
		return new DeliveryBlockTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
}
