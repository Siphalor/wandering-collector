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
import de.siphalor.tweed4.annotated.AConfigConstraint;
import de.siphalor.tweed4.annotated.AConfigEntry;
import de.siphalor.tweed4.annotated.ATweedConfig;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.constraints.RangeConstraint;

@ATweedConfig(file = WanderingCollector.MOD_ID, scope = ConfigScope.SMALLEST, environment = ConfigEnvironment.UNIVERSAL, casing = CaseFormat.LOWER_HYPHEN, tailors = "tweed4:coat")
public class WCConfig {
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

	@AConfigEntry(
			constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"),
			comment = "The minimum emerald price to buy back items"
	)
	public static int minEmeraldsTrade = 16;
	@AConfigEntry(
			constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"),
			comment = "The maximum emerald price to buy back items"
	)
	public static int maxEmeraldsTrade = 64;
}
