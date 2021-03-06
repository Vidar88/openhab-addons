/*
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openhab.binding.spacetrack.internal.client;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.openhab.binding.spacetrack.internal.client.predicate.Predicate;
import org.openhab.binding.spacetrack.internal.client.query.QueryField;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for querying the satellites' 5 latest TLEs from <a href="https://www.space-track.org/">Space-Track.org</a>. The
 * class follows the builder pattern: the query is constructed using methods like
 * {@link #addPredicate(Predicate)} and then executed with {@link #execute()}.
 * 
 * @author Steven Paligo
 */
public class LatestTleQuery
        extends Query<LatestTleQuery.LatestTleQueryField, LatestTleQuery.LatestTle, LatestTleQuery> {

    public LatestTleQuery() {

        super("tle_latest", LatestTle.class);
    }

    /**
     * Fields referenced in "latest TLE" queries on <a href="https://www.space-track.org/">Space-Track.org</a>.
     * 
     * @author Steven Paligo
     * @see LatestTleQuery
     */
    public static enum LatestTleQueryField implements QueryField {

        ORDINAL {

            @Override
            public String getQueryFieldName() {
                return "ORDINAL";
            }
        },

        NORAD_CAT_ID {

            @Override
            public String getQueryFieldName() {
                return "NORAD_CAT_ID";
            }
        },

        COMMENT {

            @Override
            public String getQueryFieldName() {
                return "COMMENT";
            }
        },

        ORIGINATOR {

            @Override
            public String getQueryFieldName() {
                return "ORIGINATOR";
            }
        },

        CATALOG_NUMBER {

            @Override
            public String getQueryFieldName() {
                return "NORAD_CAT_ID";
            }
        },

        OBJECT_NAME {

            @Override
            public String getQueryFieldName() {
                return "OBJECT_NAME";
            }
        },

        OBJECT_TYPE {

            @Override
            public String getQueryFieldName() {
                return "OBJECT_TYPE";
            }
        },

        CLASSIFICATION {

            @Override
            public String getQueryFieldName() {
                return "CLASSIFICATION_TYPE";
            }
        },

        INTERNATIONAL_DESIGNATOR {

            @Override
            public String getQueryFieldName() {
                return "INTLDES";
            }
        },

        EPOCH_YMD_HMS {

            @Override
            public String getQueryFieldName() {
                return "EPOCH";
            }
        },

        EPOCH_MICROSECONDS {

            @Override
            public String getQueryFieldName() {
                return "EPOCH_MICROSECONDS";
            }
        },

        MEAN_MOTION_REVS_PER_DAY {

            @Override
            public String getQueryFieldName() {
                return "MEAN_MOTION";
            }
        },

        ECCENTRICITY {

            @Override
            public String getQueryFieldName() {
                return "ECCENTRICITY";
            }
        },

        INCLINATION_DEGREES {

            @Override
            public String getQueryFieldName() {
                return "INCLINATION";
            }
        },

        RIGHT_ASC_OF_NODE_DEGREES {

            @Override
            public String getQueryFieldName() {
                return "RA_OF_ASC_NODE";
            }
        },

        ARG_OF_PERIGEE_DEGREES {

            @Override
            public String getQueryFieldName() {
                return "ARG_OF_PERICENTER";
            }
        },

        MEAN_ANOMALY_DEGREES {

            @Override
            public String getQueryFieldName() {
                return "MEAN_ANOMALY";
            }
        },

        EPHEMERIS_TYPE {

            @Override
            public String getQueryFieldName() {
                return "EPHEMERIS_TYPE";
            }
        },

        ELEMENT_SET_NUMBER {

            @Override
            public String getQueryFieldName() {
                return "ELEMENT_SET_NO";
            }
        },

        REV_NUMBER {

            @Override
            public String getQueryFieldName() {
                return "REV_AT_EPOCH";
            }
        },

        BSTAR {

            @Override
            public String getQueryFieldName() {
                return "BSTAR";
            }
        },

        MEAN_MOTION_DOT {

            @Override
            public String getQueryFieldName() {
                return "MEAN_MOTION_DOT";
            }
        },

        MEAN_MOTION_DOUBLE_DOT {

            @Override
            public String getQueryFieldName() {
                return "MEAN_MOTION_DDOT";
            }
        },

        FILE_NUMBER {

            @Override
            public String getQueryFieldName() {
                return "FILE";
            }
        },

        TLE_LINE0 {

            @Override
            public String getQueryFieldName() {
                return "TLE_LINE0";
            }
        },

        TLE_LINE1 {

            @Override
            public String getQueryFieldName() {
                return "TLE_LINE1";
            }
        },

        TLE_LINE2 {

            @Override
            public String getQueryFieldName() {
                return "TLE_LINE2";
            }
        },

        OBJECT_ID {

            @Override
            public String getQueryFieldName() {
                return "OBJECT_ID";
            }
        },

        OBJECT_NUMBER {

            @Override
            public String getQueryFieldName() {
                return "OBJECT_NUMBER";
            }
        },

        SEMI_MAJOR_AXIS_KILOMETERS {

            @Override
            public String getQueryFieldName() {
                return "SEMIMAJOR_AXIS";
            }
        },

        PERIOD_MINUTES {

            @Override
            public String getQueryFieldName() {
                return "PERIOD";
            }
        },

        APOGEE_HEIGHT_KILOMETERS {

            @Override
            public String getQueryFieldName() {
                return "APOGEE";
            }
        },

        PERIGEE_HEIGHT_KILOMETERS {

            @Override
            public String getQueryFieldName() {
                return "PERIGEE";
            }
        }
    }

    /**
     * Class representing results returned from "latest TLE" queries on
     * <a href="https://www.space-track.org/">Space-Track.org</a>.
     * 
     * @author Steven Paligo
     * @see LatestTleQuery
     */

    @JsonInclude(value = Include.NON_NULL)
    public static class LatestTle {

        @JsonProperty("ORDINAL")
        private Integer ordinal;

        @JsonProperty("COMMENT")
        private String comment;

        @JsonProperty("ORIGINATOR")
        private String originator;

        @JsonProperty("NORAD_CAT_ID")
        private Integer catalogNumber;

        @JsonProperty("OBJECT_NAME")
        private String objectName;

        @JsonProperty("OBJECT_TYPE")
        private Optional<String> objectType;

        @JsonProperty("CLASSIFICATION_TYPE")
        private String classification;

        @JsonProperty("INTLDES")
        private Optional<String> internationalDesignator;

        @JsonProperty("EPOCH")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant epochYmdHms;

        @JsonProperty("EPOCH_MICROSECONDS")
        private Integer epochMicroseconds;

        @JsonProperty("MEAN_MOTION")
        private Double meanMotionRevsPerDay;

        @JsonProperty("ECCENTRICITY")
        private Double eccentricity;

        @JsonProperty("INCLINATION")
        private Double inclinationDegrees;

        @JsonProperty("RA_OF_ASC_NODE")
        private Double rightAscOfNodeDegrees;

        @JsonProperty("ARG_OF_PERICENTER")
        private Double argOfPerigeeDegrees;

        @JsonProperty("MEAN_ANOMALY")
        private Double meanAnomalyDegrees;

        @JsonProperty("EPHEMERIS_TYPE")
        private Integer ephemerisType;

        @JsonProperty("ELEMENT_SET_NO")
        private Integer elementSetNumber;

        @JsonProperty("REV_AT_EPOCH")
        private Float revNumber;

        @JsonProperty("BSTAR")
        private Double bstar;

        @JsonProperty("MEAN_MOTION_DOT")
        private Double meanMotionDot;

        @JsonProperty("MEAN_MOTION_DDOT")
        private Double meanMotionDoubleDot;

        @JsonProperty("FILE")
        private Integer fileNumber;

        @JsonProperty("TLE_LINE0")
        private String tleLine0;

        @JsonProperty("TLE_LINE1")
        private String tleLine1;

        @JsonProperty("TLE_LINE2")
        private String tleLine2;

        @JsonProperty("OBJECT_ID")
        private Optional<String> objectId;

        @JsonProperty("OBJECT_NUMBER")
        private Optional<Integer> objectNumber;

        @JsonProperty("SEMIMAJOR_AXIS")
        private Double semiMajorAxisKilometers;

        @JsonProperty("PERIOD")
        private Optional<Double> periodMinutes;

        /**
         * Approximate height of the apogee assuming two-body motion and a spherical Earth with radius 6378.135 km
         */
        @JsonProperty("APOGEE")
        private Double apogeeHeightKilometers;

        /**
         * Approximate height of the perigee assuming two-body motion and a spherical Earth with radius 6378.135 km
         */
        @JsonProperty("PERIGEE")
        private Double perigeeHeightKilometers;

        @JsonProperty("DECAYED")
        private String decayed;

        public Instant getEpoch() {
            return getEpochYmdHms().plus(getEpochMicroseconds(), ChronoUnit.MICROS);
        }

        public Double getApogeeRadiusKilometers() {
            return (getSemiMajorAxisKilometers() * (1.0 + getEccentricity()));
        }

        public Double getPerigeeRadiusKilometers() {
            return (getSemiMajorAxisKilometers() * (1.0 - getEccentricity()));
        }

        public Integer getOrdinal() {
            return ordinal;
        }

        public String getComment() {
            return comment;
        }

        public String getOriginator() {
            return originator;
        }

        public Integer getCatalogNumber() {
            return catalogNumber;
        }

        public String getObjectName() {
            return objectName;
        }

        public Optional<String> getObjectType() {
            return objectType;
        }

        public String getClassification() {
            return classification;
        }

        public Optional<String> getInternationalDesignator() {
            return internationalDesignator;
        }

        public Instant getEpochYmdHms() {
            return epochYmdHms;
        }

        public Integer getEpochMicroseconds() {
            return epochMicroseconds;
        }

        public Double getMeanMotionRevsPerDay() {
            return meanMotionRevsPerDay;
        }

        public Double getEccentricity() {
            return eccentricity;
        }

        public Double getInclinationDegrees() {
            return inclinationDegrees;
        }

        public Double getRightAscOfNodeDegrees() {
            return rightAscOfNodeDegrees;
        }

        public Double getArgOfPerigeeDegrees() {
            return argOfPerigeeDegrees;
        }

        public Double getMeanAnomalyDegrees() {
            return meanAnomalyDegrees;
        }

        public Integer getEphemerisType() {
            return ephemerisType;
        }

        public Integer getElementSetNumber() {
            return elementSetNumber;
        }

        public Float getRevNumber() {
            return revNumber;
        }

        public Double getBstar() {
            return bstar;
        }

        public Double getMeanMotionDot() {
            return meanMotionDot;
        }

        public Double getMeanMotionDoubleDot() {
            return meanMotionDoubleDot;
        }

        public Integer getFileNumber() {
            return fileNumber;
        }

        public String getTleLine0() {
            return tleLine0;
        }

        public String getTleLine1() {
            return tleLine1;
        }

        public String getTleLine2() {
            return tleLine2;
        }

        public Optional<String> getObjectId() {
            return objectId;
        }

        public Optional<Integer> getObjectNumber() {
            return objectNumber;
        }

        public Double getSemiMajorAxisKilometers() {
            return semiMajorAxisKilometers;
        }

        public Optional<Double> getPeriodMinutes() {
            return periodMinutes;
        }

        public Double getApogeeHeightKilometers() {
            return apogeeHeightKilometers;
        }

        public Double getPerigeeHeightKilometers() {
            return perigeeHeightKilometers;
        }

        public String getDecayed() {
            return decayed;
        }
    }
}
