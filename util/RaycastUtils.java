package dev.mark.system.util;

import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class RaycastUtils {
    private static final MinecraftClient aiY = MinecraftClient.method_1551();

    public static boolean a(LivingEntity livingentity, Vec3d vec3d, double d0) {
        if (aiY.field_1724 == null) {
            return false;
        }

        Vec3d vec3d1 = aiY.field_1724.method_33571();
        Box box = livingentity.method_5829();
        Vec3d vec3d2 = vec3d1.method_1019(vec3d.method_1020(vec3d1).method_1029().method_1021(d0));
        return box.method_1006(vec3d2) || box.method_992(vec3d1, vec3d2).isPresent();
    }

    public static boolean b(Vec3d vec3d, Vec3d vec3d1) {
        if (aiY.field_1687 == null) {
            return false;
        }

        RaycastContext raycastcontext = new RaycastContext(vec3d, vec3d1, ShapeType.field_17558, FluidHandling.field_1348, aiY.field_1724);
        BlockHitResult blockhitresult = aiY.field_1687.method_17742(raycastcontext);
        if (blockhitresult.method_17783() == Type.field_1333) {
            return false;
        }

        BlockPos pos = blockhitresult.method_17777();
        BlockState blockstate = aiY.field_1687.method_8320(pos);
        return !c(blockstate, pos);
    }

    public static boolean c(BlockState blockstate, BlockPos pos) {
        Block block = blockstate.method_26204();
        return block instanceof DoorBlock
            || block instanceof TrapdoorBlock
            || block instanceof SlabBlock
            || block instanceof StairsBlock
            || block instanceof FenceBlock
            || block instanceof FenceGateBlock
            || block instanceof WallBlock
            || block instanceof PaneBlock
            || block instanceof SkullBlock
            || block instanceof BannerBlock
            || block instanceof SignBlock
            || block instanceof ButtonBlock
            || block instanceof AbstractPressurePlateBlock
            || block instanceof CarpetBlock
            || block instanceof FlowerBlock
            || block instanceof TorchBlock
            || block instanceof LanternBlock
            || block instanceof AbstractCandleBlock
            || !blockstate.method_26234(aiY.field_1687, pos)
            || !blockstate.method_26225();
    }
}
