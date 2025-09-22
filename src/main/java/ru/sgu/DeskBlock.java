package ru.sgu;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DeskBlock extends HorizontalFacingBlock {
	public static final MapCodec<DeskBlock> CODEC = Block.createCodec(DeskBlock::new);
	public static final EnumProperty<BedPart> PART = Properties.BED_PART;

	public DeskBlock(AbstractBlock.Settings settings) {
		super(settings);
		setDefaultState(
			getDefaultState()
			.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
			.with(Properties.BED_PART, BedPart.FOOT)
		);
	}

	@Override
	protected MapCodec<? extends DeskBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
		builder.add(Properties.BED_PART);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getHorizontalPlayerFacing().rotateYClockwise();
		BlockPos pos = ctx.getBlockPos();
		BlockPos headPos = pos.offset(direction);
		World world = ctx.getWorld();
		if(
			world.getBlockState(headPos).canReplace(ctx)
			&& world.getWorldBorder().contains(headPos)
		) {
			return super.getPlacementState(ctx)
				.with(Properties.HORIZONTAL_FACING, direction)
				.with(Properties.BED_PART, BedPart.FOOT);
		}
		return null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if(!world.isClient) {
			Direction direction = state.get(FACING);
			BlockPos headPos = pos.offset(direction);
			world.setBlockState(headPos, state.with(Properties.BED_PART, BedPart.HEAD), Block.NOTIFY_ALL);
			world.updateNeighbors(headPos, Blocks.AIR);
			state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, 
                                              BlockState neighborState, WorldAccess world, 
                                              BlockPos pos, BlockPos neighborPos) {
        
        Direction facing = state.get(FACING);
        BedPart part = state.get(PART);
        if (direction == (part == BedPart.FOOT ? facing : facing.getOpposite())) {
            if (!neighborState.isOf(this) || neighborState.get(PART) == part) {
                return Blocks.AIR.getDefaultState();
            }
        }
        
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

	@Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            BedPart part = state.get(PART);
            Direction facing = state.get(FACING);
            
            if (part == BedPart.FOOT) {
                BlockPos headPos = pos.offset(facing);
                BlockState headState = world.getBlockState(headPos);
                
                if (headState.isOf(this) && headState.get(PART) == BedPart.HEAD) {
                    world.setBlockState(headPos, Blocks.AIR.getDefaultState(), 
                        Block.NOTIFY_ALL | Block.SKIP_DROPS);
                }
            }
        }
        
        return super.onBreak(world, pos, state, player);
    }
    
    @Override
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        // обе части должны иметь одинаковый сид для согласованного рендеринга
        BlockPos leftPos = pos.offset(state.get(FACING), 
            state.get(PART) == BedPart.HEAD ? -1 : 0);
        return MathHelper.hashCode(leftPos.getX(), pos.getY(), leftPos.getZ());
    }

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}
}
