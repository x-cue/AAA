package com.xcue.lib.items;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ItemStack {
    private static final Style LORE_STYLE;
    private ItemStack() {

    }
    static {
        LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    }
    public static List<Text> getLore(@NotNull net.minecraft.item.ItemStack item) {
        List<Text> lore = new ArrayList<>();
        NbtCompound nbtCompound = item.getOrCreateNbt().getCompound("display");

        if (nbtCompound.getType("Lore") == 9) {
            NbtList nbtList = nbtCompound.getList("Lore", 8);

            for(int j = 0; j < nbtList.size(); ++j) {
                String string = nbtList.getString(j);

                try {
                    MutableText mutableText2 = Text.Serialization.fromJson(string);
                    if (mutableText2 != null) {
                        lore.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                    }
                } catch (Exception var19) {
                    nbtCompound.remove("Lore");
                }
            }
        }

        return lore;
    }

    public static void setLore(@NotNull net.minecraft.item.ItemStack item, List<Text> lore) {
        NbtCompound nbtCompound = item.getOrCreateNbt();
        NbtCompound display = item.getOrCreateSubNbt("display");
        NbtList nbtList = new NbtList();

        nbtList.addAll(lore.stream().map(x -> NbtString.of(Text.Serialization.toJsonString(x))).toList());
        display.put("Lore", nbtList);
        nbtCompound.put("display", display);
    }
}