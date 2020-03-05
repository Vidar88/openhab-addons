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


import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;

import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.openhab.binding.spacetrack.internal.client.LatestTleQuery;
import org.openhab.binding.spacetrack.internal.client.SpacetrackClient;
import org.openhab.binding.spacetrack.internal.entity.JsonEvent;
import org.openhab.binding.spacetrack.internal.handler.detection.VisibilityHandler;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.events.handlers.RecordAndContinue;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.time.UTCScale;
import org.orekit.utils.IERSConventions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.*;

/**
 * The {@link SpacetrackBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Michael Gregorius - Initial contribution
 */
public class SpacetrackBridgeHandler extends BaseBridgeHandler {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_BRIDGE);

    private final Logger logger = LoggerFactory.getLogger(SpacetrackBridgeHandler.class);

    private @Nullable SpacetrackBridgeConfiguration config;

    private @NonNullByDefault({})
    SpacetrackBridgeConfiguration bridgeConfiguration;
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
            logger.error("Make sure to set a value for the field 'Username' in Bridge configuration!");
            result = false;
        }

        // Check if password is set
        if (StringUtils.isBlank(bridgeConfiguration.spacetrackPass)) {
            logger.error("Make sure to set a value for the field 'Password' in Bridge configuration!");
            result = false;
        }

        // Check if location lat is set
        if (StringUtils.isBlank(bridgeConfiguration.locationLat)) {
            logger.error("Make sure to set a value for the field 'Latitude' in Bridge configuration!");
            result = false;
        }

        // Check if location lon is set
        if (StringUtils.isBlank(bridgeConfiguration.locationLon)) {
            logger.error("Make sure to set a value for the field 'Longitude' in Bridge configuration!");
            result = false;
        }

        // Check if location lon is set
        if (StringUtils.isBlank(bridgeConfiguration.locationAlt)) {
            logger.warn("Make sure to set a value for the field 'Altitude' in Bridge configuration for better propagation!");
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
     * Initializes the services and SpacetrackClient.
     */
    private void getSpacetrackData() {
        final ScheduledFuture<?> localReinitJob = reinitJob;
        if (localReinitJob != null && !localReinitJob.isDone()) {
            logger.info("Scheduling reinitialize in {} hours - ignored: already triggered in {} hours.", bridgeConfiguration.tleUpdateTime,
                    localReinitJob.getDelay(TimeUnit.HOURS));
            return;
        }

        logger.debug("getSpaceTrackData");
        List<String> noradIDs = new ArrayList<String>();
        for (Thing thing : getThing().getThings()) {
            noradIDs.add((String) thing.getConfiguration().get("noradID"));
        }

        String queryIDs = String.join(",", noradIDs);

        final SpacetrackClient client = new SpacetrackClient(bridgeConfiguration.spacetrackUser, bridgeConfiguration.spacetrackPass);
        List<LatestTleQuery.LatestTle> tleData = client.getTLEData(queryIDs);

        // TODO: Finally we got the data \o/
        updateState(CHANNEL_LAST_UPDATE, new DateTimeType(ZonedDateTime.now()));

        for (LatestTleQuery.LatestTle tleEntry : tleData) {
            //logger.error("Calculating overpass for Satellite {}", tleEntry.getCatalogNumber());
            TLE tle = new TLE(tleEntry.getTleLine1(), tleEntry.getTleLine2());

            Calendar calendar = Calendar.getInstance();

            AbsoluteDate initialDate = new AbsoluteDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    TimeScalesFactory.getUTC());

            TLEPropagator propagator = TLEPropagator.selectExtrapolator(tle);

            // Earth and frame
            double ae = 6378137.0; // equatorial radius in meter
            double f = 1.0 / 298.257223563; // flattening
            Frame ITRF2005 = FramesFactory.getITRF(IERSConventions.IERS_2010, true); // terrestrial frame at an arbitrary date
            BodyShape earth = new OneAxisEllipsoid(ae, f, ITRF2005);

            double lat = FastMath.toRadians(Double.parseDouble(this.bridgeConfiguration.locationLat));
            double lon = FastMath.toRadians(Double.parseDouble(this.bridgeConfiguration.locationLon));
            double altitude = Double.parseDouble(this.bridgeConfiguration.locationAlt);
            final GeodeticPoint geodeticPoint = new GeodeticPoint(lat, lon, altitude);
            TopocentricFrame stationFrame = new TopocentricFrame(earth, geodeticPoint, StringUtils.isBlank(this.bridgeConfiguration.locationName) ? "Home" : this.bridgeConfiguration.locationName);

            VisibilityHandler visibilityHandler = new VisibilityHandler(initialDate);
            // Event definition
            final double maxcheck  = 60.0;
            final double threshold =  0.001;
            final double elevation = FastMath.toRadians(5.0);
            final EventDetector homeVisible =
                    new ElevationDetector(maxcheck, threshold, stationFrame).
                            withConstantElevation(elevation).
                            withHandler(visibilityHandler);


            propagator.addEventDetector(homeVisible);

            double propagateUntil = this.bridgeConfiguration.tleUpdateTime * 7200.0;
            propagator.setSlaveMode();
            SpacecraftState finalState = propagator.propagate(initialDate, initialDate.shiftedBy(propagateUntil));

            List<VisibilityHandler.Event<ElevationDetector>> overpassEvents = visibilityHandler.getEvents();

            for (Thing thing : getThing().getThings()) {
                if (thing.getConfiguration().get("noradID").equals(tleEntry.getCatalogNumber().toString())) {
                    int index = 0;
                    if (null != overpassEvents.get(0) && !overpassEvents.get(0).isIncreasing()) {
                        index = 1;
                    }
                    List<JsonEvent> jsonEventList = new ArrayList();
                    for (VisibilityHandler.Event<ElevationDetector> overpassEvent : overpassEvents) {
                        JsonEvent event = new JsonEvent();
                        event.setIncreasing(overpassEvent.isIncreasing());
                        event.setDate(overpassEvent.getState().getDate().toString());
                        event.setMu(overpassEvent.getState().getMu());
                        event.setPosition(overpassEvent.getState().getPVCoordinates());
                        jsonEventList.add(event);
                    }
                    String jsonOverpassEvents = new Gson().toJson(jsonEventList);
                    SpacetrackSatelliteHandler handler = (SpacetrackSatelliteHandler) thing.getHandler();
                    handler.updateState(CHANNEL_OVERPASSES, new StringType(jsonOverpassEvents));
                }
            }
        }
        logger.debug("Scheduling reinitialize in {} seconds.", bridgeConfiguration.tleUpdateTime);
        reinitJob = scheduler.scheduleWithFixedDelay(this::getSpacetrackData, 0, bridgeConfiguration.tleUpdateTime,
                TimeUnit.HOURS);
    }

    private TLE getTLEForNoradID(String noradID, List<LatestTleQuery.LatestTle> tleData) {
        for (LatestTleQuery.LatestTle tle : tleData) {
            if (tle.getCatalogNumber().toString().equals(noradID)) {
                return new TLE(tle.getTleLine1(), tle.getTleLine2());
            }
        }
        return null;
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
