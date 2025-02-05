package com.artaphy.mythLibs.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

/**
 * A utility class for enhanced asynchronous logging in Bukkit plugins.
 * This class simplifies logging by automatically storing the main plugin instance.
 *
 * Supports: INFO, WARNING, SEVERE, DEBUG.
 */
object Logger {
    private lateinit var plugin: Plugin

    /** Controls whether debug messages should be logged */
    private var debugEnabled: Boolean = false

    /**
     * Initializes the logger with the provided plugin.
     * This must be called in onEnable().
     *
     * @param pluginInstance The plugin instance.
     */
    fun init(pluginInstance: Plugin, debugMode: Boolean) {
        plugin = pluginInstance
        debugEnabled = debugMode
    }

    /**
     * Logs an INFO level message.
     *
     * @param message The message to log.
     */
    fun info(message: String) {
        logAsync(Level.INFO, message)
    }

    /**
     * Logs a WARNING level message.
     *
     * @param message The message to log.
     */
    fun warn(message: String) {
        logAsync(Level.WARNING, message)
    }

    /**
     * Logs a SEVERE level message.
     *
     * @param message The message to log.
     */
    fun severe(message: String) {
        logAsync(Level.SEVERE, message)
    }

    /**
     * Logs a DEBUG level message if debug mode is enabled.
     *
     * @param message The debug message to log.
     */
    fun debug(message: String) {
        if (debugEnabled) {
            logAsync(Level.FINE, message)
        }
    }

    /**
     * Asynchronously logs a message using Bukkit's scheduler.
     *
     * @param level The log level.
     * @param message The message to log.
     */
    private fun logAsync(level: Level, message: String) {
        if (!::plugin.isInitialized) {
            throw IllegalStateException("Logger is not initialized. Call Logger.init(plugin) in onEnable().")
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            plugin.logger.log(level, message)
        })
    }
}
