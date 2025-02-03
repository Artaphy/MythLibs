package com.artaphy.mythLibs.utils

import java.util.regex.Pattern

/**
 * Utility class for handling custom color codes and formatting in Minecraft.
 *
 * This class supports the following formats:
 *
 * 1. Standard Colors:
 *    - Codes using '&' or '§' followed by a formatting shorthand (e.g. &3, &b, §3, §b)
 *    - Codes enclosed in angle brackets (e.g. <italic>, <gold>)
 *
 * 2. HEX Colors:
 *    - Format: <#XXXXXX> where XXXXXX is a 6-digit hexadecimal color code.
 *
 * 3. Gradient Colors:
 *    - Regex Pattern:
 *      `<(?<type>gradient|g)(#(?<speed>\d+))?(?<hex>(:#([A-Fa-f\d]{6}|[A-Fa-f\d]{3})){2,})(:(?<loop>l|L|loop))?>`
 *
 *    - Example: <gradient#10:#FF0000:#00FF00:#0000FF:l>
 *
 * 4. Rainbow Colors:
 *    - Regex Pattern:
 *      `<(?<type>rainbow|r)(#(?<speed>\d+))?(:(?<saturation>\d*\.?\d+))?(:(?<brightness>\d*\.?\d+))?(:(?<loop>l|L|loop))?>`
 *
 *    - Example: <rainbow#5:0.8:0.9:l>
 *
 * Note:
 * - This class currently replaces color codes with placeholders or direct conversions.
 * - You may wish to integrate this with the Adventure API or Spigot’s ChatColor for actual rendering.
 */
object ColorUtil {

    // Precompiled regex patterns for gradient and rainbow colors

    // Gradient color regex pattern
    private val GRADIENT_PATTERN: Pattern = Pattern.compile(
        "<(?<type>gradient|g)(#(?<speed>\\d+))?(?<hex>(:#([A-Fa-f\\d]{6}|[A-Fa-f\\d]{3})){2,})(:(?<loop>l|L|loop))?>"
    )

    // Rainbow color regex pattern
    private val RAINBOW_PATTERN: Pattern = Pattern.compile(
        "<(?<type>rainbow|r)(#(?<speed>\\d+))?(:(?<saturation>\\d*\\.?\\d+))?(:(?<brightness>\\d*\\.?\\d+))?(:(?<loop>l|L|loop))?>"
    )

    /**
     * Parses the provided string for custom color codes and returns the processed string.
     *
     * Supported color codes include:
     * - Standard color codes with '&' or '§'
     * - Angle bracket formatting codes (e.g. <italic>, <gold>)
     * - HEX color codes in the format <#XXXXXX>
     * - Gradient and Rainbow codes using the regex patterns defined above.
     *
     * @param input The input string containing custom color codes.
     * @return A string with color formatting placeholders replaced.
     */
    fun parseColors(input: String): String {
        var result = input

        // 1. Process standard color codes:
        // Replace occurrences of '&' or '§' followed by a valid formatting character with the section symbol '§'
        result = result.replace(Regex("[&§]([0-9a-fA-Fk-oK-OrR])")) { matchResult ->
            "§${matchResult.groupValues[1]}"
        }

        // 2. Process angle bracket formatting codes:
        // Replace codes like <italic> or <gold> with a placeholder using the first letter in lowercase.
        // This is a simplified conversion; in a real scenario, you should map these to the correct formatting codes.
        result = result.replace(Regex("<(\\w+)>")) { matchResult ->
            "§${matchResult.groupValues[1].first().lowercase()}"
        }

        // 3. Process HEX colors:
        // Convert codes like <#FF00FF> to Minecraft's hex color format.
        // Note: Modern Minecraft versions support hex colors natively.
        result = result.replace(Regex("<#([A-Fa-f\\d]{6})>")) { matchResult ->
            // Example conversion: §x§F§F§0§0§F§F
            val hex = matchResult.groupValues[1].toCharArray().joinToString(separator = "§") { it.toString() }
            "§x§$hex"
        }

        // 4. Process Gradient Colors:
        // Replace gradient patterns with a placeholder for demonstration.
        val gradientMatcher = GRADIENT_PATTERN.matcher(result)
        val gradientBuffer = StringBuffer()
        while (gradientMatcher.find()) {
            val speed = gradientMatcher.group("speed") ?: "default"
            val hexCodes = gradientMatcher.group("hex") // This contains sequences like :#FF0000:#00FF00:...
            val loop = gradientMatcher.group("loop") ?: "false"
            // TODO: Implement the actual gradient conversion logic.
            val replacement = "[Gradient(speed=$speed, hex=$hexCodes, loop=$loop)]"
            gradientMatcher.appendReplacement(gradientBuffer, replacement)
        }
        gradientMatcher.appendTail(gradientBuffer)
        result = gradientBuffer.toString()

        // 5. Process Rainbow Colors:
        // Replace rainbow patterns with a placeholder for demonstration.
        val rainbowMatcher = RAINBOW_PATTERN.matcher(result)
        val rainbowBuffer = StringBuffer()
        while (rainbowMatcher.find()) {
            val speed = rainbowMatcher.group("speed") ?: "default"
            val saturation = rainbowMatcher.group("saturation") ?: "default"
            val brightness = rainbowMatcher.group("brightness") ?: "default"
            val loop = rainbowMatcher.group("loop") ?: "false"
            // TODO: Implement the actual rainbow conversion logic.
            val replacement = "[Rainbow(speed=$speed, saturation=$saturation, brightness=$brightness, loop=$loop)]"
            rainbowMatcher.appendReplacement(rainbowBuffer, replacement)
        }
        rainbowMatcher.appendTail(rainbowBuffer)
        result = rainbowBuffer.toString()

        return result
    }
}
