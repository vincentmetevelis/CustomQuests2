package com.vincentmet.customquests;

import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.Triple;
import com.vincentmet.customquests.hierarchy.quest.*;
import com.vincentmet.customquests.standardcontent.tasktypes.ItemSubmitTaskType;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerCapability extends ItemStackHandler{
	private final Triple<Integer, Integer, Integer> activeSubtask = new Triple<>(-1, -1, -1);
	private Player player;
	private BlockEntity te;
	
	public ItemStackHandlerCapability(Player player, BlockEntity te){
		super(1);
		this.te = te;
		this.player = player;
	}
	
	public void setActiveSubtask(int quest, int task, int subtask){
		activeSubtask.setL(quest);
		activeSubtask.setM(task);
		activeSubtask.setR(subtask);
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Triple<Integer, Integer, Integer> getActiveSubtask(){
		return activeSubtask;
	}
	
	@Override
	public void onContentsChanged(int slot){
		if(player!=null&&activeSubtask.getLeft() >= 0 && activeSubtask.getMiddle() >= 0 && activeSubtask.getRight() >= 0){//todo DELIVERYBLOCK add some more uuid checks here
			if(getStackInSlot(slot).getCount()>0){
				CombinedProgressHelper.addValue(player.getUUID(), activeSubtask.getLeft(), activeSubtask.getMiddle(), activeSubtask.getRight(), 1);
				setStackInSlot(slot, new ItemStack(getStackInSlot(slot).getItem(), getStackInSlot(slot).getCount()-1));
			}
		}
		te.setChanged();
	}
	
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack){
		Task task = QuestHelper.getTaskFromId(activeSubtask.getLeft(), activeSubtask.getMiddle());
		SubTask subtask = QuestHelper.getSubtaskFromId(activeSubtask.getLeft(), activeSubtask.getMiddle(), activeSubtask.getRight());
		if(task!=null &&subtask!=null && subtask.getSubtask() instanceof ItemSubmitTaskType){
			return ((ItemSubmitTaskType)subtask.getSubtask()).getItemStacks().stream().anyMatch(itemStack -> ItemStack.isSame(itemStack, stack));
		}else{
			return false;
		}
	}
	
	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
		if(!isItemValid(slot, stack)){
			return stack;
		}
		return super.insertItem(slot, stack, simulate);
	}
	
	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate){
		return ItemStack.EMPTY;
	}
}
