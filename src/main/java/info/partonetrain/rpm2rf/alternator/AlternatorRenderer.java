package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;


public class AlternatorRenderer extends KineticBlockEntityRenderer {

    public AlternatorRenderer(Context context) {
        super(context);
    }

    /*
    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
        return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                getRenderedBlockState(be));
    }

    protected void renderShaft(AlternatorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be, be.getBlockState()), ms,
                buffer.getBuffer(RenderType.solid()), light);
    }

     */
}