package de.siphalor.wanderingcollector.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.config.constraints.AnnotationConstraint;
import de.siphalor.wanderingcollector.WanderingCollector;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

import java.util.Collections;

public class ConfigRegistryConstraint<T> implements AnnotationConstraint<String> {
	private Registry<T> registry;

	@SuppressWarnings("unused")
	public ConfigRegistryConstraint() {
		this(null);
	}

	public ConfigRegistryConstraint(Registry<T> registry) {
		this.registry = registry;
	}

	private Identifier getRegistryId() {
		//noinspection unchecked,rawtypes
		return ((Registry) Registry.REGISTRIES).getId(registry);
	}

	@Override
	public Result<String> apply(String value) {
		try {
			Identifier id = new Identifier(value);
			if (!registry.containsId(id)) {
				return new Result<>(false, null, ImmutableList.of(
						Pair.of(Severity.WARN, "No such object in registry " + getRegistryId() + ": " + value)
				));
			}
			return new Result<>(true, value, Collections.emptyList());
		} catch (InvalidIdentifierException e) {
			return new Result<>(false, null, ImmutableList.of(
					Pair.of(Severity.ERROR, "Failed to parse identifier: " + e)
			));
		}
	}

	@Override
	public String getDescription() {
		return "Must be a valid identifier and be present in the following registry: " + getRegistryId();
	}

	@Override
	public void fromAnnotationParam(String param, Class<?> valueType) {
		//noinspection unchecked
		this.registry = (Registry<T>) Registry.REGISTRIES.get(Identifier.tryParse(param));
		if (this.registry == null) {
			WanderingCollector.log(Level.WARN, "Unknown config registry in constraint: " + param);
		}
	}
}
