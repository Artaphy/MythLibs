package com.artaphy.mythLibs

import com.artaphy.mythLibs.utils.Logger
import org.bukkit.plugin.java.JavaPlugin

class MythLibs : JavaPlugin() {


    override fun onEnable() {
        Logger.init(this)
        Logger.info("MythLibs successfully enabled!")
    }

    override fun onDisable() {

        Logger.info("MythLibs successfully disabled!")
    }
}
