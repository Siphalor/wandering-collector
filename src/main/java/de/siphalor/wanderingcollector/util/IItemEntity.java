package de.siphalor.wanderingcollector.util;

import java.util.UUID;

public interface IItemEntity {
	void wanderingCollector$setFormerOwner(UUID playerUuid);
	UUID wanderingCollector$getFormerOwner();
}
