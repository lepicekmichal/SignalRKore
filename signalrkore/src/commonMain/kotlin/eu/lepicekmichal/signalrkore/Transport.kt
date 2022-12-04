// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.
package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.flow.Flow

internal interface Transport {
    suspend fun start(url: String)
    suspend fun send(message: ByteArray)
    fun receive(): Flow<ByteArray>
    suspend fun stop()
}