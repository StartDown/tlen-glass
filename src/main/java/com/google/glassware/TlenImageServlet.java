package com.google.glassware;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import org.mortbay.log.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class TlenImageServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(TlenImageServlet.class.getSimpleName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("tlen servlet do post");

        String userId = AuthUtil.getUserId(req);
        final Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
        String message = "";

        if (req.getParameter("operation").equals("insertItem")) {
            LOG.info("Inserting tlen item");
            final TimelineItem timelineItem = new TimelineItem();

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

            if (req.getParameter("imageUrl") != null) {
                // Attach an image, if we have one
                URL url = new URL(req.getParameter("imageUrl"));
                final String contentType = req.getParameter("contentType");

                LOG.info("tlenify image : " + url.toString());
                ImageProcessingUtil.tlenifyImage(3, url.toString(), new ImageProcessingUtil.TlenCallback() {
                    @Override
                    public void tlen(String imageUrl) {
                        try {
                            LOG.info("tlen finished : " + imageUrl);
                            URL tlenUrl = new URL(imageUrl);
                            MirrorClient.insertTimelineItem(credential, timelineItem, contentType, tlenUrl.openStream());
                        } catch (MalformedURLException e) {
                            LOG.info(e.getMessage());
                        } catch (IOException e) {
                            LOG.info(e.getMessage());
                        }
                    }
                });

            } else {
                MirrorClient.insertTimelineItem(credential, timelineItem);
            }
        }
    }
}
