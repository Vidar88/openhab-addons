/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.spacetrack.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link SpacetrackBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Michael Gregorius - Initial contribution
 */
@NonNullByDefault
public class SpacetrackBindingConstants {

    private static final String BINDING_ID = "spacetrack";

    public static final String SPACETRACK_BASE_URL = "https://www.space-track.org/ajaxauth/login";

    // Things
    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_TYPE_SATELLITE = new ThingTypeUID(BINDING_ID, "satellite");

    public static final Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES = Collections.unmodifiableSet(
            Stream.of(THING_TYPE_SATELLITE).collect(Collectors.toSet()));

    // List of all Channel ids
    public static final String CHANNEL_LAST_UPDATE = "lastUpdate";
    public static final String CHANNEL_NORAD_ID = "noradID";
    public static final String CHANNEL_DISPLAY_NAME = "displayName";
}
