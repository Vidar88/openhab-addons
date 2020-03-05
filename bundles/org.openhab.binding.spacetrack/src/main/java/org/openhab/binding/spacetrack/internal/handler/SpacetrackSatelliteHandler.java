/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.spacetrack.internal.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static org.eclipse.smarthome.core.thing.ThingStatus.OFFLINE;
import static org.eclipse.smarthome.core.thing.ThingStatus.ONLINE;
import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.*;

/**
 * The {@link SpacetrackSatelliteHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Michael Gregorius - Initial contribution
 */
@NonNullByDefault
public class SpacetrackSatelliteHandler extends BaseThingHandler {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = SUPPORTED_DEVICE_THING_TYPES;

    private final Logger logger = LoggerFactory.getLogger(SpacetrackSatelliteHandler.class);

    private @Nullable SpacetrackSatelliteConfiguration config;

    public SpacetrackSatelliteHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand called for channel '{}' of type '{}' with command '{}'", channelUID,
                getThing().getThingTypeUID().getId(), command);

        if (command instanceof RefreshType) {
            updateSatelliteData(channelUID);
        }

        if (command instanceof StringType) {
            updateState(channelUID, (StringType) command);
        }

        // TODO: handle command

        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information:
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");

    }

    private void updateSatelliteData(ChannelUID channelUID) {
        switch (channelUID.getId()) {
            case CHANNEL_NORAD_ID:
                updateState(CHANNEL_NORAD_ID, new StringType(config.noradID));
                break;
            case CHANNEL_DISPLAY_NAME:
                updateState(CHANNEL_DISPLAY_NAME, new StringType(config.displayName));
                break;
            default:
        }
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing satellite!");
        this.config = getConfigAs(SpacetrackSatelliteConfiguration.class);

        if (StringUtils.isBlank(config.displayName)) {
            config.displayName = config.noradID;
        }

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            // when done do:
            if (!getBridge().getStatus().equals(ONLINE)) {
                updateStatus(OFFLINE);
            } else {
                updateStatus(ONLINE);
            }
        });

        // logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        super.bridgeStatusChanged(bridgeStatusInfo);

        if (!bridgeStatusInfo.getStatus().equals(ONLINE)) {
            updateStatus(OFFLINE);
        } else {
            updateStatus(ONLINE);
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);

        for (Map.Entry<String, Object> entry : configurationParameters.entrySet()) {
            if (entry.getValue() instanceof String) {
                updateState(entry.getKey(), new StringType((String) entry.getValue()));
            }
        }
    }

    @Override
    protected void updateState(String channelID, State state) {
        super.updateState(channelID, state);
    }
}
