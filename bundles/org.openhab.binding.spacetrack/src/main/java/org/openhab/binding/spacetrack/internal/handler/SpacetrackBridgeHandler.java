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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.auth.client.oauth2.OAuthClientService;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.spacetrack.internal.client.SpacetrackClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.CHANNEL_LAST_UPDATE;
import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.THING_TYPE_BRIDGE;

/**
 * The {@link SpacetrackBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Michael Gregorius - Initial contribution
 */
@NonNullByDefault
public class SpacetrackBridgeHandler extends BaseBridgeHandler {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_BRIDGE);

    private final Logger logger = LoggerFactory.getLogger(SpacetrackBridgeHandler.class);

    private @Nullable SpacetrackBridgeConfiguration config;

    private @NonNullByDefault({}) SpacetrackBridgeConfiguration bridgeConfiguration;
    private @Nullable ScheduledFuture<?> reinitJob;

    public SpacetrackBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Start handleCommand!");
        if (CHANNEL_LAST_UPDATE.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        final SpacetrackBridgeConfiguration bridgeConfiguration = getConfigAs(SpacetrackBridgeConfiguration.class);

        if (checkConfig(bridgeConfiguration)) {
            this.bridgeConfiguration = bridgeConfiguration;
            scheduler.execute(this::getSpacetrackData);
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
            if (checkConfig(bridgeConfiguration)) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        logger.debug("Finished initializing!");
        // logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    /**
     * Checks bridge configuration. If configuration is valid returns true.
     *
     * @return true if the configuration if valid
     */
    private boolean checkConfig(final SpacetrackBridgeConfiguration bridgeConfiguration) {
        boolean result = true;
        // Check if username is set
        if (StringUtils.isBlank(bridgeConfiguration.spacetrackUser)) {
            logger.warn("Make sure to set a value for the field 'Username' in Bridge configuration!");
            result = false;
        }

        // Check if password is set
        if (StringUtils.isBlank(bridgeConfiguration.spacetrackPass)) {
            logger.warn("Make sure to set a value for the field 'Password' in Bridge configuration!");
            result = false;
        }

        // Check if location lat is set
        if (StringUtils.isBlank(bridgeConfiguration.locationLat)) {
            logger.warn("Make sure to set a value for the field 'Latitude' in Bridge configuration!");
            result = false;
        }

        // Check if location lon is set
        if (StringUtils.isBlank(bridgeConfiguration.locationLon)) {
            logger.warn("Make sure to set a value for the field 'Longitude' in Bridge configuration!");
            result = false;
        }

        // Precheck to keep traffic for sapce-track.org as low as possible
        if (!result) {
            return false;
        }

        if (!checkSpacetrackAuth(bridgeConfiguration)) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid Spacetrack credentials." +
                    " Make sure to set a valid username and password in the bridge configuration!");
            result = false;
        }

        return result;
    }

    /**
     * Initializes the services and InnogyClient.
     */
    private void getSpacetrackData() {
        logger.error("getSpaceTrackData");
       /* final OAuthClientService oAuthService = oAuthFactory.createOAuthClientService(thing.getUID().getAsString(),
                API_URL_TOKEN, API_URL_TOKEN, bridgeConfiguration.clientId, bridgeConfiguration.clientSecret, null,
                true);
        this.oAuthService = oAuthService;

        if (checkOnAuthCode()) {

            client = localClient;
            deviceStructMan = new DeviceStructureManager(localClient);
            oAuthService.addAccessTokenRefreshListener(this);
            registerDeviceStatusListener(InnogyBridgeHandler.this);
            scheduleRestartClient(0);
        }*/
    }

    /**
     * Checks for valid Spacetrack credentials
     */
    private boolean checkSpacetrackAuth(final SpacetrackBridgeConfiguration bridgeConfiguration) {
        if (StringUtils.isBlank(bridgeConfiguration.spacetrackUser) || StringUtils.isBlank(bridgeConfiguration.spacetrackPass)) {
            return false;
        }

        final SpacetrackClient client = new SpacetrackClient(bridgeConfiguration.spacetrackUser, bridgeConfiguration.spacetrackPass);
        if (!client.checkSpaceTrackAuth()) {
            return false;
        }

        return true;
    }
}
