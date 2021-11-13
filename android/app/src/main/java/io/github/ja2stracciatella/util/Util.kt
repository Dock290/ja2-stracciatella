package io.github.ja2stracciatella.util

class Util {
    companion object {
        fun validateResolution(resolutionString: String): Boolean {
            val widthHeight = resolutionString.split('x')
            if (widthHeight.size != 2) {
                return false
            }
            val width = widthHeight[0].toIntOrNull()
            val height = widthHeight[1].toIntOrNull()
            return width is Int && width >= 640
                    && height is Int && height >= 480
        }
    }
}