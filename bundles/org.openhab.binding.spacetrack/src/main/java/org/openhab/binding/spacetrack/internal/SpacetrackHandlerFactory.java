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

import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.spacetrack.internal.handler.SpacetrackBridgeHandler;
import org.openhab.binding.spacetrack.internal.handler.SpacetrackSatelliteHandler;
import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link SpacetrackHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Michael Gregorius - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.spacetrack", service = ThingHandlerFactory.class)
public class SpacetrackHandlerFactory extends BaseThingHandlerFactory implements ThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(SpacetrackHandlerFactory.class);

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Stream
            .concat(SpacetrackBridgeHandler.SUPPORTED_THING_TYPES.stream(),
                    SpacetrackSatelliteHandler.SUPPORTED_THING_TYPES.stream())
            .collect(Collectors.toSet());

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_BRIDGE.equals(thingTypeUID)) {
            return new SpacetrackBridgeHandler((Bridge) thing);
        } else if (THING_TYPE_SATELLITE.equals(thingTypeUID)) {
            return new SpacetrackSatelliteHandler(thing);
        } else {
            logger.debug("Unsupported thing {}.", thing.getThingTypeUID());
            return null;
        }
    }

    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);

        // Load orekit initial data.
        DataProvidersManager manager = DataProvidersManager.getInstance();
        manager.addProvider(new ClasspathCrawler("orekit-data.zip"));
    }
}
