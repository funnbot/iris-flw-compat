package top.leonx.irisflw.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.foundation.render.RenderTypes;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.RenderType;

@Mixin(RenderTypes.class)
public class MixinRenderTypes {
    @Inject(method = "getAdditive", at = @At("HEAD"), remap = false, cancellable = true)
    private static void getAdditive(CallbackInfoReturnable<RenderType> cir) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            cir.setReturnValue(RenderType.translucent());
        }
    }
}
