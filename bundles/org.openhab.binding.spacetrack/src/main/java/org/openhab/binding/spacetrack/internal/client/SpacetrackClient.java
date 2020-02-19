package org.openhab.binding.spacetrack.internal.client;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.spacetrack.internal.client.credential.DefaultCredentialProvider;
import org.openhab.binding.spacetrack.internal.client.predicate.Equal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.openhab.binding.spacetrack.internal.SpacetrackBindingConstants.SPACETRACK_BASE_URL;

@NonNullByDefault
public class SpacetrackClient {

    private final Logger logger = LoggerFactory.getLogger(SpacetrackClient.class);

    private String username;
    private String password;

    public SpacetrackClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean checkSpaceTrackAuth() {
        HttpsURLConnection connection = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(SPACETRACK_BASE_URL);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // send the request (log in and query at the same time)
            String request = "identity=" + this.username + "&password=" + this.password;

            outputStream = connection.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                logger.error("Invalid Spacetrack credentials." +
                        " Make sure to set a valid username and password in the bridge configuration!");
                return false;
            }

            // read the entire response
            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
            if (response.contains("Login") && response.contains("Failed")) {
                logger.error("Invalid Spacetrack credentials." +
                        " Make sure to set a valid username and password in the bridge configuration!");
                return false;
            }
            logger.debug("SpaceTrack response message: {}", connection.getResponseMessage());
            logger.debug("SpaceTrack response body: {}", response);

        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            // clean up
            try {
                outputStream.close();
            } catch (Exception e) {
                logger.error("An exception occurred while closing the SpaceTrack request stream", e);
            }

            try {
                connection.disconnect();
            } catch (Exception e) {
                logger.error("An exception occurred while disconnecting from the SpaceTrack API", e);
            }
        }
        return true;
    }

    public List<LatestTleQuery.LatestTle> getTLEData() {
        try {
            DefaultCredentialProvider credentials = new DefaultCredentialProvider(this.username, this.password);
            List<LatestTleQuery.LatestTle> list = new LatestTleQuery().setCredentials(credentials).addPredicate(new Equal<>(LatestTleQuery.LatestTleQueryField.ORDINAL, 1)).execute();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
