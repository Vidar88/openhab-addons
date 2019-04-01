/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.knx.internal.config;

import java.math.BigDecimal;

import org.openhab.binding.knx.internal.handler.KNXBridgeBaseThingHandler;

/**
 * {@link KNXBridgeBaseThingHandler} configuration
 *
 * @author Simon Kaufmann - initial contribution and API
 *
 */
public class BridgeConfiguration {

    private BigDecimal autoReconnectPeriod;
    private BigDecimal readingPause;
    private BigDecimal readRetriesLimit;
    private BigDecimal responseTimeout;

    public BigDecimal getAutoReconnectPeriod() {
        return autoReconnectPeriod;
    }

    public BigDecimal getReadingPause() {
        return readingPause;
    }

    public BigDecimal getReadRetriesLimit() {
        return readRetriesLimit;
    }

    public BigDecimal getResponseTimeout() {
        return responseTimeout;
    }

}