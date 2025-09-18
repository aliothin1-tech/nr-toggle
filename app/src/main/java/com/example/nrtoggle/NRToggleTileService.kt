package com.example.nrtoggle
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.*

class NRToggleTileService : TileService() {
    private val subId = 0
    override fun onStartListening() {
        super.onStartListening()
        CoroutineScope(Dispatchers.Main).launch {
            val state = withContext(Dispatchers.IO) { getCurrentMode() }
            qsTile.state = if (state == MODE_NR_ONLY) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.updateTile()
        }
    }
    override fun onClick() {
        super.onClick()
        CoroutineScope(Dispatchers.Main).launch {
            val current = withContext(Dispatchers.IO) { getCurrentMode() }
            val target = if (current == MODE_NR_ONLY) MODE_NR_LTE else MODE_NR_ONLY
            withContext(Dispatchers.IO) { setMode(target) }
            qsTile.state = if (target == MODE_NR_ONLY) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.updateTile()
        }
    }
    private fun getCurrentMode(): Int {
        val res = ShellUtils.runRootCommand("settings get global preferred_network_mode1")
        return res.output.trim().toIntOrNull() ?: MODE_NR_LTE
    }
    private fun setMode(mode: Int) {
        ShellUtils.runRootCommand("settings put global preferred_network_mode1 $mode")
        ShellUtils.runRootCommand("settings put global preferred_network_mode $mode")
    }
    companion object {
        const val MODE_NR_ONLY = 20
        const val MODE_NR_LTE = 33
    }
}