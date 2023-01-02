/*
 * Copyright 2021-2023 Siphalor
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

package de.siphalor.wanderingcollector.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.config.constraints.AnnotationConstraint;
import de.siphalor.wanderingcollector.WanderingCollector;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
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
		return ((Registry) Registries.REGISTRIES).getId(registry);
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
		this.registry = (Registry<T>) Registries.REGISTRIES.get(Identifier.tryParse(param));
		if (this.registry == null) {
			WanderingCollector.log(Level.WARN, "Unknown config registry in constraint: " + param);
		}
	}
}
