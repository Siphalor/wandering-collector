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

package de.siphalor.wanderingcollector;

import de.siphalor.wanderingcollector.util.IItemEntity;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class WanderingCollector implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "wandering_collector";
    public static final String MOD_NAME = "Wandering Collector";

    public static final String LOST_STACKS_KEY = MOD_ID + ":" + "lost_stacks";
    public static final String PLAYER_SPECIFIC_TRADES = MOD_ID + ":" + "player_specific_trades";

	public static final TagKey<Item> DENY_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "deny"));

	@Override
    public void onInitialize() {
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }


	public static void addStackToThrower(ItemEntity item) {
		if (item.getStack().isIn(DENY_TAG)) {
			return;
		}

		Entity theFormerOwner = null;
		if (WCConfig.includeDroppedStacks) {
			theFormerOwner = item.getOwner();
		}
		if (theFormerOwner == null && item instanceof IItemEntity) {
			UUID ownerUuid = ((IItemEntity) item).wanderingCollector$getFormerOwner();
            if (ownerUuid != null) {
                theFormerOwner = item.world.getPlayerByUuid(ownerUuid);
            }
		}
		if (theFormerOwner instanceof IServerPlayerEntity) {
            ((IServerPlayerEntity) theFormerOwner).wandering_collector$getLostItemStorage().add(item.getStack());
		}
	}
}
