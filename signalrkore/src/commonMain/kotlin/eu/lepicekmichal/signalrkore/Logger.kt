package eu.lepicekmichal.signalrkore

fun interface Logger {
    fun log(level: Level, message: String)

    enum class Level {
        INFO,
        WARNING,
        ERROR,
    }
}