package carpetfixes.mixins.blockUpdates.duplicateUpdates;

import carpetfixes.CFSettings;
import carpetfixes.helpers.BlockUpdateUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fix duplicate block updates in the redstone torch code
 */

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlock_updateMixin extends TorchBlock {

    RedstoneTorchBlock self = (RedstoneTorchBlock)(Object)this;

    protected RedstoneTorchBlock_updateMixin(Settings settings, ParticleEffect particle) {
        super(settings, particle);
    }


    @Inject(
            method = "onStateReplaced",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStateReplacedBetter(BlockState state, World world, BlockPos pos,
                                      BlockState newState, boolean moved, CallbackInfo ci) {
        if (!moved) { //Was missing: !state.isOf(newState.getBlock()) && state.get(LIT)
            if (CFSettings.duplicateBlockUpdatesFix) {
                if (!state.isOf(newState.getBlock()) && state.get(RedstoneTorchBlock.LIT)) {
                    BlockUpdateUtils.doExtendedBlockUpdates(
                            world,
                            pos,
                            self,
                            state.get(RedstoneTorchBlock.LIT) & !newState.equals(state),
                            true
                    );
                }
                ci.cancel();
            } else { // Do Settings.uselessSelfBlockUpdateFix here
                if (CFSettings.useCustomRedstoneUpdates) {
                    boolean doExtraEarlyUpdate = state.get(RedstoneTorchBlock.LIT) & !newState.equals(state);
                    BlockUpdateUtils.doExtendedBlockUpdates(world, pos, self, doExtraEarlyUpdate, true);
                    ci.cancel();
                }
            }
        }
    }


    @Inject(
            method = "onBlockAdded",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onBlockAddedBetter(BlockState state, World world, BlockPos pos,
                                   BlockState newState, boolean moved, CallbackInfo ci) {
        if (CFSettings.useCustomRedstoneUpdates) {
            BlockUpdateUtils.doExtendedBlockUpdates(world, pos, self, false, true);
            ci.cancel();
        }
    }
}
