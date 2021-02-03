package de.siphalor.wanderingcollector;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WanderingCollector implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "wandering_collector";
    public static final String MOD_NAME = "Wandering Collector";

    public static final String LOST_STACKS_KEY = MOD_ID + ":" + "lost_stacks";
    public static final String PLAYER_SPECIFIC_TRADES = MOD_ID + ":" + "player_specific_trades";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        //TODO: Initializer
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}
