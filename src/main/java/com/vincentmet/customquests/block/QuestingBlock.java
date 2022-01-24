package com.vincentmet.customquests.block;

import com.vincentmet.customquests.api.ClientUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

public class QuestingBlock extends Block{
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public QuestingBlock(){
		super(Properties.of(new Material.Builder(MaterialColor.COLOR_BLACK).build()).harvestLevel(0).strength(1F).noCollission());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(world.isClientSide()){
			ClientUtils.openQuestingScreen();
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.SUCCESS;
	}
	@SuppressWarnings("deprecation")
	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getVisualShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	
}
