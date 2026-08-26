package dev.mark.system.render.entity;

import com.mojang.authlib.GameProfile;
import dev.mark.system.util.UnsafeFieldAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class FakePlayerRenderer {
    private OtherClientPlayerEntity nT;
    private Vec3d Mk;
    private float cx;
    private float ht;
    private float ahx;
    private float lZ;
    private boolean Y;
    private boolean afm;
    private float aan;
    private float wW;
    private float Rw;
    private float Zu;
    private boolean lx;
    private int Md;
    private Hand aV;
    private float Qu;
    private EntityPose mc;
    private int fP;
    private final MinecraftClient asE = MinecraftClient.method_1551();
    private UnsafeFieldAccessor<Float> EC;
    private UnsafeFieldAccessor<Float> ayq;
    private UnsafeFieldAccessor<Float> sX;
    private UnsafeFieldAccessor<Float> bk;
    private UnsafeFieldAccessor<Float> HP;
    private UnsafeFieldAccessor<Float> acq;

    private void a(LimbAnimator limbanimator) {
        this.EC = new UnsafeFieldAccessor<>(limbanimator, LimbAnimator.class, 0);
        this.ayq = new UnsafeFieldAccessor<>(limbanimator, LimbAnimator.class, 1);
        this.sX = new UnsafeFieldAccessor<>(limbanimator, LimbAnimator.class, 2);
        this.bk = new UnsafeFieldAccessor<>(limbanimator, LimbAnimator.class, 3);
    }

    private void b(LivingEntity livingentity) {
        this.HP = new UnsafeFieldAccessor<>(livingentity, LivingEntity.class, 103);
        this.acq = new UnsafeFieldAccessor<>(livingentity, LivingEntity.class, 104);
    }

    public void c(AbstractClientPlayerEntity abstractclientplayerentity) {
        if (abstractclientplayerentity != null && this.asE.field_1687 != null) {
            this.Mk = abstractclientplayerentity.method_19538();
            this.cx = abstractclientplayerentity.method_36454();
            this.ht = abstractclientplayerentity.method_36455();
            this.ahx = abstractclientplayerentity.field_6283;
            this.lZ = abstractclientplayerentity.field_6241;
            this.Y = abstractclientplayerentity.method_5715();
            this.afm = abstractclientplayerentity.method_5624();
            this.a(abstractclientplayerentity.field_42108);
            this.b(abstractclientplayerentity);
            this.wW = abstractclientplayerentity.field_42108.method_48566();

            try {
                this.aan = this.sX.getFloat();
                this.Rw = this.bk.getFloat();
            } catch (Exception exception1) {
                this.aan = 0.0F;
                this.Rw = 1.0F;
            }

            this.Zu = abstractclientplayerentity.field_6251;
            this.lx = abstractclientplayerentity.field_6252;
            this.Md = abstractclientplayerentity.field_6279;
            this.aV = abstractclientplayerentity.method_6058();

            try {
                this.Qu = this.HP.getFloat();
            } catch (Exception exception) {
                this.Qu = 0.0F;
            }

            this.mc = abstractclientplayerentity.method_18376();
            this.fP = abstractclientplayerentity.field_6012;
            this.d(abstractclientplayerentity, this.asE.field_1687);
        }
    }

    private void d(AbstractClientPlayerEntity abstractclientplayerentity, ClientWorld clientworld) {
        if (this.nT == null) {
            GameProfile gameprofile = abstractclientplayerentity.method_7334();
            this.nT = new OtherClientPlayerEntity(clientworld, gameprofile);
            this.e();
            this.f(abstractclientplayerentity);
        }
    }

    private void e() {
        if (this.nT != null) {
            this.nT.method_33574(this.Mk);
            this.nT.field_6014 = this.Mk.field_1352;
            this.nT.field_6036 = this.Mk.field_1351;
            this.nT.field_5969 = this.Mk.field_1350;
            this.nT.field_6038 = this.Mk.field_1352;
            this.nT.field_5971 = this.Mk.field_1351;
            this.nT.field_5989 = this.Mk.field_1350;
            this.nT.method_36456(this.cx);
            this.nT.method_36457(this.ht);
            this.nT.field_5982 = this.cx;
            this.nT.field_6004 = this.ht;
            this.nT.field_6283 = this.ahx;
            this.nT.field_6220 = this.ahx;
            this.nT.field_6241 = this.lZ;
            this.nT.field_6259 = this.lZ;
            this.nT.method_5660(this.Y);
            this.nT.method_5728(this.afm);
            this.nT.method_18380(this.mc);
            this.nT.field_6012 = this.fP;
            this.a(this.nT.field_42108);
            this.b(this.nT);

            try {
                float f = this.aan + this.wW;
                this.sX.setFloat(f);
                this.ayq.setFloat(this.wW);
                this.EC.setFloat(this.wW);
                this.bk.setFloat(this.Rw);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }

            this.nT.field_6251 = this.Zu;
            this.nT.field_6229 = this.Zu;
            this.nT.field_6252 = this.lx;
            this.nT.field_6279 = this.Md;
            this.nT.field_6266 = this.aV;
            this.nT.method_6021();

            try {
                this.HP.setFloat(this.Qu);
                this.acq.setFloat(this.Qu);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void f(AbstractClientPlayerEntity abstractclientplayerentity) {
        if (this.nT != null) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                this.nT.method_5673(equipmentslot, abstractclientplayerentity.method_6118(equipmentslot).method_7972());
            }
        }
    }

    public void g(WorldRenderContext worldrendercontext) {
        if (this.nT != null && this.Mk != null && this.asE.field_1687 != null) {
            MatrixStack matrixstack = worldrendercontext.matrixStack();
            Vec3d vec3d = worldrendercontext.camera().method_19326();
            float f = 0.0F;
            this.e();
            double d0 = this.Mk.field_1352 - vec3d.field_1352;
            double d1 = this.Mk.field_1351 - vec3d.field_1351;
            double d2 = this.Mk.field_1350 - vec3d.field_1350;
            EntityRenderDispatcher entityrenderdispatcher = this.asE.method_1561();
            Immediate immediate = this.asE.method_22940().method_23000();
            int i = entityrenderdispatcher.method_23839(this.nT, f);
            entityrenderdispatcher.method_62424(this.nT, d0, d1, d2, f, matrixstack, immediate, i);
            immediate.method_22993();
        }
    }

    public void h() {
        this.nT = null;
        this.Mk = null;
    }

    public boolean i() {
        return this.nT != null && this.Mk != null;
    }
}
