package com.blocksaffinity.crops;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class SoundParser {

    /**
     * Parses a string into a Bukkit Sound, with a fallback default.
     *
     * @param input   The raw sound string (e.g., "ENTITY_PLAYER_LEVELUP" or "block.anvil.place")
     * @param fallback The default sound string if the input is missing or invalid
     * @return A valid org.bukkit.Sound instance, or null if both choices fail
     */
    public static Sound parseSound(String input, String fallback) {
        // AS I WAS LOOKING THROUGH THIS CODE
        // I noticed ENTITY_EXPERIENCE_ORB_PICKUP
        // would be parsed to entity.experience.orb.pickup
        // WHICH IS NOT A SOUND
        // so DON'T USE SNAKE CAPS IN CONFIG PLZ
        // THEY ARE NO LONGER SUPPORTED

        // 1. Attempt to parse primary user input
        Sound chosenSound = getSoundFromRegistry(input);
        if (chosenSound != null) {
            return chosenSound;
        }

        // 2. If primary fails, attempt to parse the fallback sound
        Sound defaultSound = getSoundFromRegistry(fallback);
        if (defaultSound != null) {
            return defaultSound;
        }

        // 3. Absolute ultimate safety fallback if everything goes wrong
        return Registry.SOUNDS.get(NamespacedKey.minecraft("entity.player.levelup"));
    }

    /**
     * Helper method to map legacy SCREAMING_CAPS or modern dotted strings
     * to a valid registered Bukkit Sound.
     */
    private static Sound getSoundFromRegistry(String rawSound) {
        if (rawSound == null || rawSound.isEmpty()) {
            return null;
        }

        // Normalise input: Registry requires lowercase and replaces underscores with dots for vanilla matching
        // e.g., "ENTITY_PLAYER_LEVELUP" -> "entity.player.levelup"
        String cleaned = rawSound.toLowerCase().trim();//.replace("_", ".");

        // Handle namespaced structures safely
        NamespacedKey key = NamespacedKey.fromString(cleaned);
        if (key == null) {
            return null;
        }

        return Registry.SOUNDS.get(key);
    }
}
