/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.wanderingcollector;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed4.annotated.AConfigBackground;
import de.siphalor.tweed4.annotated.AConfigConstraint;
import de.siphalor.tweed4.annotated.AConfigEntry;
import de.siphalor.tweed4.annotated.ATweedConfig;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.constraints.RangeConstraint;
import de.siphalor.wanderingcollector.util.ConfigRegistryConstraint;
import de.siphalor.wanderingcollector.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

import java.util.Random;

@ATweedConfig(file = WanderingCollector.MOD_ID, scope = ConfigScope.SMALLEST, environment = ConfigEnvironment.UNIVERSAL, casing = CaseFormat.LOWER_HYPHEN, tailors = "tweed4:coat")
@AConfigBackground("minecraft:textures/block/blue_concrete_powder.png")
public class WCConfig {
	private static final Random random = new Random();

	@AConfigEntry(
			comment = "Include manually dropped items in the Wandering Trader offers"
	)
	public static boolean includeDroppedStacks = false;

	@AConfigEntry(
			constraints = @AConfigConstraint(value = RangeConstraint.class, param = "0.."),
			comment = "The maximum number of items that get remembered after loosing them"
	)
	public static int maxLostStackAmount = 64;
	@AConfigEntry(
			constraints = @AConfigConstraint(value = RangeConstraint.class, param = "0.."),
			comment = "The number of buy-back trades Wandering Traders will offer"
	)
	public static int buyBackTrades = 2;

	public static PriceDefinition defaultPrices;

	@AConfigBackground("minecraft:textures/block/green_terracotta.png")
	public static class PriceDefinition {
		@AConfigEntry(
				constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"),
				comment = "The minimum emerald price to buy back a stack"
		)
		public int minPrice = 32;

		@AConfigEntry(
				constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"),
				comment = "The maximum emerald price to buy back a stack"
		)
		public int maxPrice = 64;

		@AConfigEntry(
				constraints = @AConfigConstraint(value = ConfigRegistryConstraint.class, param = "minecraft:item"),
				comment = "The item that must be paid with for the trade offer"
		)
		public String priceItem = "minecraft:emerald";

		@AConfigEntry(
				comment = "Whether the price will be scaled with the amount of items in the stack."
		)
		public boolean scalePriceWithCount = true;

		@AConfigEntry(
				constraints = @AConfigConstraint(value = RangeConstraint.class, param = "0..1"),
				comment = "The minimum percentage that scale-price-with-amount will scale to."
		)
		public float minPriceScale = 0.3f;

		public ItemStack getPriceStack(ItemStack forStack) {
			Item priceItem = Registry.ITEM.get(new Identifier(this.priceItem));
			if (priceItem == Items.AIR) {
				WanderingCollector.log(Level.WARN, "Unknown price item \"" + this.priceItem + "\", defaulting to minecraft:emerald");
				priceItem = Items.EMERALD;
			}
			ItemStack result = new ItemStack(priceItem);

			int count = Utils.randInclusive(random, this.minPrice, this.maxPrice);
			if (scalePriceWithCount) {
				count = MathHelper.ceil(count * (minPriceScale + (1 - minPriceScale) * forStack.getCount() / forStack.getMaxCount()));
			}
			result.setCount(count);
			return result;
		}
	}
}
