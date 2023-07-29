package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import info.partonetrain.rpm2rf.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import info.partonetrain.rpm2rf.shapes.ModShapes;


public class AlternatorBlock extends DirectionalKineticBlock implements IBE<AlternatorBlockEntity>, IRotate {


    //why is com.simibubi.create.AllShapes.shape(double x1, double y1, double z1, double x2, double y2, double z2) private? :(
    public static final VoxelShaper ALTERNATOR_SHAPE = ModShapes.shape(0, 3, 0, 16, 13, 16).add(2, 0, 2, 14, 14, 14).forDirectional();


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ALTERNATOR_SHAPE.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred);
    }

    public AlternatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }

    @Override
    public BlockEntityType<? extends AlternatorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ALTERNATOR.get();
    }

    @Override
    public Class<AlternatorBlockEntity> getBlockEntityClass() {
        return AlternatorBlockEntity.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.ALTERNATOR.create(pos, state);
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.SLOW; //SpeedLevel.SLOW is anything below 30 rpm (by default Create config)
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        if(be != null) {
            if(be instanceof AlternatorBlockEntity) {
                ((AlternatorBlockEntity)be).updateCache();
            }
        }
    }
}