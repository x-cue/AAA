package com.xcue.mods.notahflipper.util;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AhSoldListing {
        private final List<Text> lore;
        private final Text name;
        private final double soldPrice;
        private final int amount;
        private final String sellerName;
        private final String buyerName;
        private final ZonedDateTime dateListed;
        private final ZonedDateTime dateSold;
        private final ItemStack item;

        public AhSoldListing(ItemStack item) throws ParseException, NumberFormatException {
                // Parse item from here
                this.item = item;
                this.lore = com.xcue.lib.items.ItemStack.getLore(item);
                this.name = item.getName();
                this.amount = item.getCount();

                // Get other fields from the item's lore
                List<Text> loreReversed = lore;
                Collections.reverse(loreReversed);

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

        private long getSecondsSinceSold(String str) {
                //examples of string before and after replace :::
                // "10d 2h 3m 20s" = "10 2 3 20"
                // "12h 10m 10s" = "12 10 10"
                // "5m 20s" = "5 20"
                // "5h 10s" = "5 10" ------ on the off chance there is an item exactly at 5 hours and no minutes. what will happen?
                Pattern pat = Pattern.compile("(\\d+)d|(\\d+)h|(\\d+)m|(\\d+)s");
                Matcher matcher = pat.matcher(str);

                long numDays = Long.parseLong(matcher.group(1) != null ? matcher.group(1) : "0");
                long numHours = Long.parseLong(matcher.group(2) != null ? matcher.group(2) : "0");
                long numMinutes = Long.parseLong(matcher.group(3) != null ? matcher.group(3) : "0");
                long numSeconds = Long.parseLong(matcher.group(4) != null ? matcher.group(4) : "0");

                return (numSeconds + (numMinutes * 60) + (numHours * 3600) + (numDays * 86400));
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
