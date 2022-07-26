package hu.bendi.betterarchery.client.entity;

import hu.bendi.betterarchery.interfaces.IHasHair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class StriderHairFeatureRenderer extends FeatureRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
    private static final Identifier TEXTURE = new Identifier("betterarchery", "textures/entity/strider_hair.png");
    private final StriderEntityModel<StriderEntity> model;

    public StriderHairFeatureRenderer(FeatureRendererContext<StriderEntity, StriderEntityModel<StriderEntity>> context, StriderEntityModel<StriderEntity> model) {
        super(context);
        this.model = model;
    }

    public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, (float) normalX, (float) normalY, (float) normalZ).next();
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, StriderEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (((IHasHair) entity).getHasHair()) {
            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

            var body = this.model.getPart().getChild(EntityModelPartNames.BODY);

            body.rotate(matrixStack);

            {
                matrixStack.push();
                MatrixStack.Entry entry = matrixStack.peek();
                Matrix4f matrix4f = entry.getPositionMatrix();
                Matrix3f matrix3f = entry.getNormalMatrix();
                this.vertex(matrix4f, matrix3f, vertexConsumer, 0.5f, -0.37f, -0.35f, 0, 1, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, 0.5f, -0.37f, 0.35f, 1, 1, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, 0.25f, -1.0f, 0.35f, 1, 0, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, 0.25f, -1.0f, -0.35f, 0, 0, -1, 0, 0, light);
                matrixStack.pop();
            }
            {
                matrixStack.push();
                MatrixStack.Entry entry = matrixStack.peek();
                Matrix4f matrix4f = entry.getPositionMatrix();
                Matrix3f matrix3f = entry.getNormalMatrix();
                this.vertex(matrix4f, matrix3f, vertexConsumer, -0.25f, -1.0f, -0.35f, 0, 0, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, -0.25f, -1.0f, 0.35f, 1, 0, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, -0.5f, -0.37f, 0.35f, 1, 1, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumer, -0.5f, -0.37f, -0.35f, 0, 1, 1, 0, 0, light);
                matrixStack.pop();
            }
        }
    }
}
