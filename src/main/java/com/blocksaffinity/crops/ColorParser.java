package com.blocksaffinity.crops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorParser {

    // Matches legacy hex codes like &#aabbcc or &#AABBCC
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");


    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        // convert legacy hex &#rrggbb to <#rrggbb>
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        String step1String = sb.toString();

        // make a component structure out of legacy codes
        Component legacyParsedComponent = LegacyComponentSerializer.builder()
                .character('&')
                .hexCharacter('#')
                .extractUrls()     // preserve URLs
                .build()
                .deserialize(step1String);

        // turn it back into a string that the minimessage deserializer can understand
        String globalMiniMessageString = MiniMessage.miniMessage().serialize(legacyParsedComponent);

        // convert it to minimessage by deserializing
        return MiniMessage.miniMessage().deserialize(globalMiniMessageString);
    }
}
