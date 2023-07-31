package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;


public class AlternatorRenderer<T extends KineticBlockEntity> extends KineticBlockEntityRenderer<T> {

    public AlternatorRenderer(Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }

}