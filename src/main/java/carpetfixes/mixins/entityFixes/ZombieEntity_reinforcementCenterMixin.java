package carpetfixes.mixins.entityFixes;

import carpetfixes.CFSettings;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes zombie reinforcements spawning off centered by 0.5, entities are always suppose to spawn in the middle of
 * blocks and this is a clear violation. How dare they!
 */

@Mixin(ZombieEntity.class)
public class ZombieEntity_reinforcementCenterMixin {


    @Redirect(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/ZombieEntity;setPosition(DDD)V",
                    ordinal = 0
            )
    )
    private void modifyType(ZombieEntity entity, double x, double y, double z) {
        if (CFSettings.reinforcementsSpawnOffCenteredFix) {
            entity.setPosition(x+.5,y,z+.5);
        } else {
            entity.setPosition(x,y,z);
        }
    }
}
