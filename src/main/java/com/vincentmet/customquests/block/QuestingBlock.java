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
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public QuestingBlock(){
		super(Properties.create(new Material.Builder(MaterialColor.BLACK).build()).harvestLevel(0).hardnessAndResistance(1F).doesNotBlockMovement());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(world.isRemote){
			ClientUtils.openQuestingScreen();
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
	
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
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
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, .7, 1));
	}
	
	
}
