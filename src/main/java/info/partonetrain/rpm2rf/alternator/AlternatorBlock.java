package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import info.partonetrain.rpm2rf.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlternatorBlock extends RotatedPillarKineticBlock implements IBE<AlternatorBlockEntity>, IRotate {

    //there has to be a better way to do this lol
    public static final VoxelShape ALTERNATOR_X_SHAPE = Block.box(1, 2, 2, 15, 14, 14);
    public static final VoxelShape ALTERNATOR_Y_SHAPE = Block.box(2, 1, 2, 14, 15, 14);
    public static final VoxelShape ALTERNATOR_Z_SHAPE = Block.box(2, 2, 1, 14, 14, 15);

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if(getRotationAxis(state) == Axis.X){
            return ALTERNATOR_X_SHAPE;
        }else if(getRotationAxis(state) == Axis.Y){
            return ALTERNATOR_Y_SHAPE;
        }else{
            return ALTERNATOR_Z_SHAPE;
        }

    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return getCollisionShape(p_60555_, p_60556_, p_60557_, p_60558_);
    }

    public AlternatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
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

}