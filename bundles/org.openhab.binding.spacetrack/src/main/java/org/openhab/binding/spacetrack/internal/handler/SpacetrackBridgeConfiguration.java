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
package org.openhab.binding.spacetrack.internal.handler;

/**
 * The {@link SpacetrackBridgeConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Michael Gregorius - Initial contribution
 */
public class SpacetrackBridgeConfiguration {

    /**
     * Sample configuration parameter. Replace with your own.
     */
    public String spacetrackUser;
    public String spacetrackPass;
    public String locationName;

    public String locationLat;
    public String locationLon;

    public int tleUpdateTime;
}
