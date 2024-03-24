package eu.lepicekmichal.signalrkore

import platform.Foundation.NSUUID

actual object UUID {
    actual fun randomUUID(): String = NSUUID().UUIDString()
}