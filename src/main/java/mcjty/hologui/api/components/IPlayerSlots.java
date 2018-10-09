package mcjty.hologui.api.components;

import mcjty.hologui.api.IGuiComponent;
import mcjty.hologui.api.IStackEvent;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public interface IPlayerSlots extends IGuiComponent {

    // Which items from the player inventory should be shown
    IPlayerSlots filter(Predicate<ItemStack> matcher);

    IPlayerSlots hitEvent(IStackEvent event);

}