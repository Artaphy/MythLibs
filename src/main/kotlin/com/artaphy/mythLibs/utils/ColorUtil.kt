package com.artaphy.mythLibs.utils

import java.awt.Color
import java.util.regex.Pattern
import kotlin.math.roundToInt

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
 *    - Regex Pattern (with text content):
 *      `<(gradient|g)(#(?<speed>\d+))?(?<hex>(:#([A-Fa-f\d]{6}|[A-Fa-f\d]{3})){2,})(:(?<loop>l|L|loop))?>(?<text>.*?)</(gradient|g)>`
 *
 *    - Example:
 *      `<gradient#10:#FF0000:#00FF00:#0000FF:l>This text will have a gradient color</gradient>`
 *
 * 4. Rainbow Colors:
 *    - Regex Pattern (with text content):
 *      `<(rainbow|r)(#(?<speed>\d+))?(:(?<saturation>\d*\.?\d+))?(:(?<brightness>\d*\.?\d+))?(:(?<loop>l|L|loop))?>(?<text>.*?)</(rainbow|r)>`
 *
 *    - Example:
 *      `<rainbow#5:0.8:0.9:l>This text will have a rainbow effect</rainbow>`
 *
 * Note:
 * - The methods in this class produce Minecraft color codes in the format:
 *   §x§R§R§G§G§B§B for hex colors. Modern Minecraft supports these natively.
 */
object ColorUtil {

    // Precompiled regex patterns for gradient and rainbow with text
    private val GRADIENT_PATTERN: Pattern = Pattern.compile(
        "<(?<type>gradient|g)(#(?<speed>\\d+))?(?<hex>(:#([A-Fa-f\\d]{6}|[A-Fa-f\\d]{3})){2,})(:(?<loop>l|L|loop))?>(?<text>.*?)</(gradient|g)>",
        Pattern.DOTALL
    )
    private val RAINBOW_PATTERN: Pattern = Pattern.compile(
        "<(?<type>rainbow|r)(#(?<speed>\\d+))?(:(?<saturation>\\d*\\.?\\d+))?(:(?<brightness>\\d*\\.?\\d+))?(:(?<loop>l|L|loop))?>(?<text>.*?)</(rainbow|r)>",
        Pattern.DOTALL
    )

    /**
     * Parses the provided string for custom color codes and returns the processed string.
     *
     * Supported color codes include:
     * - Standard color codes with '&' or '§'
     * - Angle bracket formatting codes (e.g. <italic>, <gold>)
     * - HEX color codes in the format <#XXXXXX>
     * - Gradient and Rainbow codes wrapping text
     *
     * @param input The input string containing custom color codes.
     * @return A string with color formatting replaced with Minecraft color codes.
     */
    fun parseColors(input: String): String {
        var result = input

        // 1. Process standard color codes:
        result = result.replace(Regex("[&§]([0-9a-fA-Fk-oK-OrR])")) { matchResult ->
            "§${matchResult.groupValues[1]}"
        }

        // 2. Process angle bracket formatting codes:
        result = result.replace(Regex("<(\\w+)>")) { matchResult ->
            "§${matchResult.groupValues[1].first().lowercase()}"
        }

        // 3. Process HEX colors:
        result = result.replace(Regex("<#([A-Fa-f\\d]{6})>")) { matchResult ->
            val hex = matchResult.groupValues[1]
            hexToMinecraftFormat("#$hex")
        }

        // 4. Process Gradient Colors:
        val gradientMatcher = GRADIENT_PATTERN.matcher(result)
        val gradientBuffer = StringBuffer()
        while (gradientMatcher.find()) {
            // Optional speed parameter (unused in interpolation but available for future use)
            gradientMatcher.group("speed")?.toIntOrNull() ?: 0
            // Extract all hex codes from the captured group.
            val hexGroup = gradientMatcher.group("hex") // e.g. ":#FF0000:#00FF00:#0000FF"
            // Find all occurrences of :#XXXXXX in the hexGroup.
            val hexRegex = Regex(":(#[A-Fa-f\\d]{6}|#[A-Fa-f\\d]{3})")
            val hexCodes = hexRegex.findAll(hexGroup).map { it.groupValues[1] }.toList()
            val loop = gradientMatcher.group("loop") != null
            val text = gradientMatcher.group("text") ?: ""
            val coloredText = applyGradient(text, hexCodes, loop)
            gradientMatcher.appendReplacement(gradientBuffer, coloredText)
        }
        gradientMatcher.appendTail(gradientBuffer)
        result = gradientBuffer.toString()

        // 5. Process Rainbow Colors:
        val rainbowMatcher = RAINBOW_PATTERN.matcher(result)
        val rainbowBuffer = StringBuffer()
        while (rainbowMatcher.find()) {
            val speed = rainbowMatcher.group("speed")?.toIntOrNull() ?: 0
            val saturation = rainbowMatcher.group("saturation")?.toDoubleOrNull() ?: 1.0
            val brightness = rainbowMatcher.group("brightness")?.toDoubleOrNull() ?: 1.0
            val loop = rainbowMatcher.group("loop") != null
            val text = rainbowMatcher.group("text") ?: ""
            val coloredText = applyRainbow(text, speed, saturation, brightness, loop)
            rainbowMatcher.appendReplacement(rainbowBuffer, coloredText)
        }
        rainbowMatcher.appendTail(rainbowBuffer)
        result = rainbowBuffer.toString()

        return result
    }

    /**
     * Applies a gradient effect to the provided text using the given list of hex color strings.
     *
     * @param text The text to which the gradient will be applied.
     * @param hexCodes A list of hex color strings (e.g. "#FF0000", "#00FF00", "#0000FF").
     * @param loop If true, the gradient will loop/repeat across the text.
     * @return The text with gradient color codes inserted.
     */
    private fun applyGradient(text: String, hexCodes: List<String>, loop: Boolean): String {
        if (text.isEmpty() || hexCodes.isEmpty()) return text

        // Convert hex strings to Color objects
        val colors = hexCodes.map { hexToColor(it) }
        val textLength = text.length
        val segments = colors.size - 1
        val sb = StringBuilder()

        // If looping is enabled, treat the gradient as cyclic
        val effectiveLength = if (loop) textLength else textLength - 1

        for (i in 0 until textLength) {
            // Compute relative position within the overall gradient
            val relativePos = i.toDouble() / effectiveLength
            // When not looping, clamp to 1.0 at the end.
            val pos = if (!loop && relativePos > 1.0) 1.0 else relativePos

            // Determine which segment of the gradient this character falls into.
            val segmentPos = pos * segments
            val segmentIndex = segmentPos.toInt().coerceAtMost(segments - 1)
            val segmentFraction = segmentPos - segmentIndex

            val startColor = colors[segmentIndex]
            val endColor = colors[segmentIndex + 1]
            val interpolated = interpolateColor(startColor, endColor, segmentFraction)
            sb.append(colorToMinecraftFormat(interpolated))
            sb.append(text[i])
        }
        return sb.toString()
    }

    /**
     * Applies a rainbow effect to the provided text.
     *
     * The rainbow effect is generated by varying the hue over the length of the text.
     *
     * @param text The text to color with the rainbow effect.
     * @param speed A parameter that can adjust the rate of hue change.
     * @param saturation The saturation for the rainbow colors (0.0 to 1.0).
     * @param brightness The brightness for the rainbow colors (0.0 to 1.0).
     * @param loop If true, the rainbow effect will loop.
     * @return The text with rainbow color codes inserted.
     */
    private fun applyRainbow(
        text: String,
        speed: Int,
        saturation: Double,
        brightness: Double,
        loop: Boolean
    ): String {
        if (text.isEmpty()) return text

        val sb = StringBuilder()
        val textLength = text.length.coerceAtLeast(1) // 确保不为 0，避免除以 0 错误
        val cycles = if (loop) speed.coerceAtLeast(1).toDouble() else 1.0 // 确保 speed 不是 0

        for (i in text.indices) {
            val hue = if (loop) {
                ((i.toDouble() / textLength.toDouble()) * cycles) % 1.0
            } else {
                (i.toDouble() / (textLength - 1).coerceAtLeast(1)).coerceIn(0.0, 1.0)
            }

            val color = Color.getHSBColor(hue.toFloat(), saturation.toFloat(), brightness.toFloat())
            sb.append(colorToMinecraftFormat(color))
            sb.append(text[i])
        }
        return sb.toString()
    }


    /**
     * Interpolates between two colors.
     *
     * @param start The start color.
     * @param end The end color.
     * @param fraction The interpolation fraction between 0.0 and 1.0.
     * @return The interpolated Color.
     */
    private fun interpolateColor(start: Color, end: Color, fraction: Double): Color {
        val r = start.red + ((end.red - start.red) * fraction)
        val g = start.green + ((end.green - start.green) * fraction)
        val b = start.blue + ((end.blue - start.blue) * fraction)
        return Color(r.roundToInt(), g.roundToInt(), b.roundToInt())
    }

    /**
     * Converts a hex color string (e.g. "#FF00FF") to a java.awt.Color object.
     *
     * @param hex The hex color string.
     * @return The corresponding Color object.
     */
    private fun hexToColor(hex: String): Color {
        return Color.decode(hex)
    }

    /**
     * Converts a java.awt.Color to Minecraft’s hex color code format.
     *
     * For example, a color with hex "#FF00FF" becomes "§x§F§F§0§0§F§F".
     *
     * @param color The Color to convert.
     * @return The Minecraft formatted color string.
     */
    private fun colorToMinecraftFormat(color: Color): String {
        val hex = String.format("%02X%02X%02X", color.red, color.green, color.blue)
        // Insert the formatting codes: §x followed by § each hex digit
        return "§x" + hex.toCharArray().joinToString("") { "§$it" }
    }

    /**
     * Overloaded helper to convert a hex string directly to Minecraft format.
     *
     * @param hex A hex color string (e.g. "#FF00FF").
     * @return The Minecraft formatted color string.
     */
    private fun hexToMinecraftFormat(hex: String): String {
        return colorToMinecraftFormat(hexToColor(hex))
    }
}
