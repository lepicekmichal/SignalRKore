// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.

package eu.lepicekmichal.signalrkore;

import kotlinx.serialization.Serializable

@Serializable
internal class HandshakeResponse(val error: String? = null)
