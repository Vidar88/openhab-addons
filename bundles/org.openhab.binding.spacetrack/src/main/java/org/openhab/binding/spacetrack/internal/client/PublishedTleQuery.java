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

import org.openhab.binding.spacetrack.internal.client.predicate.Predicate;
import org.openhab.binding.spacetrack.internal.client.query.QueryField;
import org.openhab.binding.spacetrack.internal.client.util.UtcInstantDeserializer;
import org.threeten.extra.scale.UtcInstant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Class for querying TLEs by their publishing date from <a href="https://www.space-track.org/">Space-Track.org</a>. The
 * class follows the builder pattern: the query is constructed using methods like
 * {@link #addPredicate(Predicate)} and then executed with {@link #execute()}.
 * 
 * @author Steven Paligo
 */
public class PublishedTleQuery
        extends Query<PublishedTleQuery.PublishedTleQueryField, PublishedTleQuery.PublishedTle, PublishedTleQuery> {

    public PublishedTleQuery() {

        super("tle_publish", PublishedTle.class);
    }

    /**
     * Fields referenced in "published TLE" queries on <a href="https://www.space-track.org/">Space-Track.org</a>.
     * 
     * @author Steven Paligo
     * @see PublishedTleQuery
     */
    public static enum PublishedTleQueryField implements QueryField {

        PUBLISH_TIME {

            @Override
            public String getQueryFieldName() {
                return "PUBLISH_EPOCH";
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
        }
    }

    /**
     * Class representing results returned from "published TLE" queries on
     * <a href="https://www.space-track.org/">Space-Track.org</a>.
     * 
     * @author Steven Paligo
     * @see PublishedTleQuery
     */

    @JsonInclude(value = Include.NON_NULL)
    public static class PublishedTle {

        @JsonProperty("PUBLISH_EPOCH")
        @JsonDeserialize(using = UtcInstantDeserializer.class)
        private UtcInstant publishTime;

        @JsonProperty("TLE_LINE1")
        private String tleLine1;

        @JsonProperty("TLE_LINE2")
        private String tleLine2;

    }
}
