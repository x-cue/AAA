package com.xcue.mods.notahflipper;

import com.xcue.lib.AAAMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotAhFlipperMod implements AAAMod {
//    ClientPlayerEntity player = MinecraftClient.getInstance().player;
//    MinecraftClient client = MinecraftClient.getInstance();
    @Override
    public void init() {

        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(!scanAH.isEnabled() || !scanAH.isOpen(client)) return;

            ClientPlayerEntity player = client.player;
            if(player != null){
                HandledScreen<?> screen = (HandledScreen<?>)  client.currentScreen;
                ScreenHandler handler = screen.getScreenHandler();
                List<ItemStack> stacks = handler.getStacks();



            }

        });

    }
    public final class scanAH {
        private static boolean scanItems = false;
        private scanAH(){
        }
        public static void enable(){
            scanAH.scanItems = true;
        }
        public static void disable(){
            scanAH.scanItems = false;
        }
        public static boolean isEnabled(){
            return scanAH.scanItems;
        }

        public static boolean isOpen(@NotNull MinecraftClient client){
            if(!scanAH.scanItems || client.currentScreen == null) return false;
            Text title = client.currentScreen.getTitle();
            if(!Objects.equals(title.getString(), "Cosmic Auction House")){
                return false;
            }
            return client.currentScreen instanceof HandledScreen<?>;
        }
    }
    public final class AhItem {

        private final List<Text> lore;
        private final Text name;
        private final double soldPrice;
        private final int amount;
        private final String sellerName;


        private final String buyerName;
        private final ZonedDateTime dateListed;
        private final ZonedDateTime dateSold;
        private ItemStack item;


        public AhItem(ItemStack item) throws ParseException, NumberFormatException {
            // Parse item from here
            this.lore = com.xcue.lib.items.ItemStack.getLore(item);
            this.name = item.getName();
            this.amount = item.getCount();

            // Get other fields from the item's lore
            List<Text> loreReversed = lore.reversed();

            this.sellerName = parseItemField(loreReversed.get(4).getString(), "Seller");
            this.buyerName = parseItemField(loreReversed.get(3).getString(), "Buyer");
            this.soldPrice = Double.parseDouble(
                    parseItemField(loreReversed.get(2).getString(), "Price")
                    .replace("$", "")
                    .replace(",", "")
            );

            // Get from lore
            long secondsSinceSold = getSecondsSinceSold(loreReversed.get(1).getString());

            this.dateSold = ZonedDateTime.now().minusSeconds(secondsSinceSold);
            this.dateListed = null;
        }

        private long getSecondsSinceSold(String str) throws ParseException {
            int days;
            int hours;
            int minutes;
            int seconds;
            String timeStr = parseItemField(str, null, "^Item Sold (.+) ago!$")
                    .replace("d", "")
                    .replace("h", "")
                    .replace("m", "")
                    .replace("s", "");
            //examples of string before and after replace :::
            // "10d 2h 3m 20s" = "10 2 3 20"
            // "12h 10m 10s" = "12 10 10"
            // "5m 20s" = "5 20"
            // "5h 10s" = "5 10" ------ on the off chance there is an item exactly at 5 hours and no minutes. what will happen?
            String[] splitStr = str.split("//s+");

        }

        private String parseItemField(String str, String field, String regex) throws ParseException {
            Pattern pat = Pattern.compile(String.format(regex, field));

            Matcher matcher = pat.matcher(str);

            if (!matcher.matches()) throw new ParseException("Could not find " + field +  " name", 0);

            return matcher.group(1);
        }

        private String parseItemField(String str, String field) throws ParseException {
            return parseItemField(str, field, "^%s: (\\S+)$");
        }

        public ItemStack getItem() {
            return item;
        }
        public List<Text> getLore() {
            return this.lore;
        }

        public Text getName() {
            return name;
        }

        public ZonedDateTime getDateSold() {
            return dateSold;
        }

        public ZonedDateTime getDateListed() {
            return dateListed;
        }

        public String getSellerName() {
            return sellerName;
        }

        public int getAmount() {
            return amount;
        }

        public double getSoldPrice() {
            return soldPrice;
        }
        public String getBuyerName() {
            return buyerName;
        }
    }

}

