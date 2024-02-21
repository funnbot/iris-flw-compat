package top.leonx.irisflw.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.foundation.render.RenderTypes;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.RenderStateShard;
import com.simibubi.create.Create;

@Mixin(RenderTypes.class)
public class MixinRenderTypes extends RenderStateShard {
    public MixinRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    /*
     * translucent
     * LightmapState = LIGHTMAP
     * TextureState = BLOCK_SHEET_MIPPED
     * TransparencyState = TRANSLUCENT_TRANSPARENCY
     * CullState = CULL
     * OutputState = TRANSLUCENT_TARGET
     * DefaultVertexFormat = BLOCK
     * AffectsCrumbling = true
     * SortOnUpload = true
     * ShaderState = RENDERTYPE_TRANSLUCENT_SHADER
     * 
     * additive
     * LightmapState = LIGHTMAP
     * TextureState = BLOCK_SHEET_MIPPED
     * TransparencyState = ADDITIVE_TRANSPARENCY
     * CullState = NO_CULL
     * OutputState = MAIN_TARGET
     * DefaultVertexFormat = BLOCK
     * AffectsCrumbling = true
     * SortOnUpload = true
     * ShaderState = BLOCK_SHADER
     * 
     * EYES:
     * ShaderState = RENDERTYPE_EYES_SHADER
     * ADDITIVE_TRANSPARENCY
     * COLOR_WRITE
     * 
     * 
     * try:
     * AffectsCrumbling = false
     * setWriteMaskState(COLOR_DEPTH_WRITE)
     * LIGHTNING_TRANSPARENCY
     */

    private static final RenderType ADDITIVE_FIXED = RenderType.create(createLayerName("additive"),
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(BLOCK_SHEET)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    // .setWriteMaskState(COLOR_DEPTH_WRITE)
                    // .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(NO_LIGHTMAP) // its emissive so it should not be affected by light
                    .setOverlayState(OVERLAY)
                    .setLayeringState(POLYGON_OFFSET_LAYERING) // apparently a technique used to fix z-fighting
                    // however, the back of the inner signal panel has z-fighting with the light
                    // source, needs to be fixed in the model file.
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(true));

    @Inject(method = "getAdditive", at = @At("HEAD"), remap = false, cancellable = true)
    private static void getAdditive(CallbackInfoReturnable<RenderType> cir) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            cir.setReturnValue(ADDITIVE_FIXED);
        }
    }

    private static String createLayerName(String name) {
        return Create.ID + ":" + name;
    }
}
