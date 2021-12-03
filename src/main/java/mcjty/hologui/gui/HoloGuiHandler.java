package mcjty.hologui.gui;

import mcjty.hologui.HoloGui;
import mcjty.hologui.ModEntities;
import mcjty.hologui.api.*;
import mcjty.hologui.gui.components.GuiComponentRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HoloGuiHandler implements IHoloGuiHandler {

    public static final String TAG_DEFAULT = "default";

    private GuiRegistry guiRegistry = new GuiRegistry();
    private GuiComponentRegistry guiComponentRegistry = new GuiComponentRegistry();
    private List<IHoloGuiProvider> providers = new ArrayList<>();

    @Override
    public Class<? extends Entity> getHoloEntityClass() {
        return HoloGuiEntity.class;
    }

    @Override
    public void registerProvider(IHoloGuiProvider provider) {
        providers.add(provider);
    }

    @Nullable
    @Override
    public IGuiTile getGuiTile(World world, BlockPos pos) {
        for (IHoloGuiProvider provider : providers) {
            IGuiTile tile = provider.getTile(world, pos);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public boolean openHoloGui(World world, BlockPos pos, PlayerEntity player) {
        IHoloGuiEntity entity = openHoloGuiEntity(world, pos, player, TAG_DEFAULT, 1.0);
        return world.isClientSide ? true : entity != null;
    }

    @Override
    public IHoloGuiEntity openHoloGuiEntity(World world, BlockPos pos, PlayerEntity player, String tag, double distance) {
        if (world.isClientSide) {
            world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), HoloGuiSounds.guiopen, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return null;
        }
        IGuiTile guiTile = HoloGui.guiHandler.getGuiTile(world, pos);
        if (guiTile == null) {
            return null;
        }

        HoloGuiEntity entity = createHoloGui(world, player, tag, distance);
        entity.setGuiTile(pos);
        return entity;
    }


    @Override
    public IGuiComponent createNoAccessPanel() {
        IGuiComponentRegistry registry = guiComponentRegistry;
        return registry.panel(0, 0, 8, 8)
                .add(registry.text(2, 3, 1, 1).text("Access").color(0xff0000))
                .add(registry.text(2, 4, 1, 1).text("Denied!").color(0xff0000))
            ;
    }

    @Override
    public IHoloGuiEntity openHoloGui(PlayerEntity player, String guiId, double distance) {
        // @todo, check what's wrong with sound

        World world = player.getCommandSenderWorld();
        BlockPos pos = player.blockPosition();

        world.playSound(player, pos, HoloGuiSounds.guiopen, SoundCategory.PLAYERS, 1.0f, 1.0f);
        if (world.isClientSide) {
            world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), HoloGuiSounds.guiopen, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return null;
        }
        HoloGuiEntity entity = createHoloGui(world, player, "", distance);
        entity.setGuiId(guiId);
        return entity;
    }

    @Override
    public IHoloGuiEntity openHoloGuiRelative(Entity parent, Vector3d offset, String guiId) {
//        if (world.isClientSide) {
//            world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.guiopen, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
//            return null;
//        }
        World world = parent.getCommandSenderWorld();
        HoloGuiEntity entity = createHoloGuiRelative(world, parent, offset, "");
        entity.setGuiId(guiId);
        return entity;
    }

    private static HoloGuiEntity createHoloGui(World world, PlayerEntity player, String tag, double distance) {
        HoloGuiEntity entity = new HoloGuiEntity(ModEntities.HOLOGUI_ENTITY_TYPE.get(), world);
        entity.setTag(tag);
        double x = player.getX();
        double y = player.getY()+player.getEyeHeight() - .5;
        double z = player.getZ();
        Vector3d lookVec = player.getLookAngle();
        lookVec = new Vector3d(lookVec.x, 0, lookVec.z).normalize();
        x += lookVec.x * distance;
        y += lookVec.y;
        z += lookVec.z * distance;
        entity.setPos(x, y, z);
        entity.moveTo(x, y, z, player.yRot, 0);
        world.addFreshEntity(entity);
        return entity;
    }

    private static HoloGuiEntity createHoloGuiRelative(World world, Entity parent, Vector3d offset, String tag) {
//        HoloGuiEntity entity = new HoloGuiEntitySmall(world);
        HoloGuiEntity entity = new HoloGuiEntity(ModEntities.HOLOGUI_ENTITY_TYPE.get(), world);
        entity.setTag(tag);
        double x = parent.getX() + offset.x;
        double y = parent.getY() + offset.y;
        double z = parent.getZ() + offset.z;
        entity.setPos(x, y, z);
        entity.moveTo(x, y, z, parent.yRot+90, parent.xRot);
        world.addFreshEntity(entity);
        return entity;
    }

    @Override
    public IGuiRegistry getGuiRegistry() {
        return guiRegistry;
    }

    @Override
    public IGuiComponentRegistry getComponentRegistry() {
        return guiComponentRegistry;
    }

    @Override
    public void render(IHoloGuiEntity entity, double x, double y, double z, float entityYaw) {
        // @todo 1.15
//        HoloGuiEntityRender.doActualRender((HoloGuiEntity) entity, x, y, z, entityYaw);
    }

}
