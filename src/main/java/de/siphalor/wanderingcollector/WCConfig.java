package de.siphalor.wanderingcollector;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.annotated.AConfigConstraint;
import de.siphalor.tweed.config.annotated.AConfigEntry;
import de.siphalor.tweed.config.annotated.ATweedConfig;
import de.siphalor.tweed.config.constraints.RangeConstraint;

@ATweedConfig(file = WanderingCollector.MOD_ID, scope = ConfigScope.SMALLEST, environment = ConfigEnvironment.UNIVERSAL, casing = CaseFormat.LOWER_HYPHEN, tailors = "tweed:cloth")
public class WCConfig {
	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "0.."))
	public static int maxLostStackAmount = 64;
	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "0.."))
	public static int buyBackTrades = 2;

	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"))
	public static int minEmeraldsTrade = 16;
	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "1..64"))
	public static int maxEmeraldsTrade = 64;
}
