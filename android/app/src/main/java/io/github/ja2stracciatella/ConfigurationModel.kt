package io.github.ja2stracciatella

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfigurationModel : ViewModel() {

    val vanillaGameDir = MutableLiveData<String>()
    val mods = MutableLiveData<List<String>>()
    val res = MutableLiveData("640x480")
    val brightness = MutableLiveData(1.0F)
    val resversion = MutableLiveData("ENGLISH")
    val fullscreen = MutableLiveData(true)
    val scaling = MutableLiveData("PERFECT")
    val debug = MutableLiveData(false)
    val nosound = MutableLiveData(false)

    fun setVanillaGameDir(vanillaGameDirSet: String) {
        vanillaGameDir.value = vanillaGameDirSet
    }

    fun setMods(modsSet: List<String>) {
        mods.value = modsSet
    }

    fun setRes(resSet: String) {
        res.value = resSet
    }

    fun setBrightness(brightnessSet: Float) {
        brightness.value = brightnessSet
    }

    fun setResversion(resverSet: String) {
        resversion.value = resverSet
    }

    fun setFullscreen(fullscreenSet: Boolean) {
        fullscreen.value = fullscreenSet
    }

    fun setScaling(resSet: String) {
        scaling.value = resSet
    }

    fun setDebug(debugSet: Boolean) {
        debug.value = debugSet
    }

    fun setNosound(nosoundSet: Boolean) {
        nosound.value = nosoundSet
    }
}