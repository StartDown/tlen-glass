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
import java.io.InputStream;
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

            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

            if (req.getParameter("imageUrl") != null) {
                String timelineItemId = req.getParameter("timelineItemId");
                String attachmentId = req.getParameter("attachmentId");

                LOG.info("timelineItemId : " + timelineItemId);
                LOG.info("attachment id : " + attachmentId);

                InputStream attachmentStream = MirrorClient
                        .getAttachmentInputStream(credential, timelineItemId, attachmentId);

//                URL url = new URL("http://cs6066.vk.me/u182884895/video/l_0ea74030.jpg");
                final String contentType = req.getParameter("contentType");

                LOG.info("tlenify image : " + req.getParameter("imageUrl"));
                final String tlenImageUrlString = ImageProcessingUtil.tlenifyImage(attachmentStream);
                LOG.info("tlenned image : " + tlenImageUrlString);
                URL tlenImageUrl = new URL(tlenImageUrlString);

                MirrorClient.insertTimelineItem(credential, timelineItem, contentType, tlenImageUrl.openStream());
            } else {
                MirrorClient.insertTimelineItem(credential, timelineItem);
            }
        }
    }
}
