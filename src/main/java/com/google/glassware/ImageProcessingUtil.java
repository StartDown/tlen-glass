package com.google.glassware;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yarik on 7/27/13.
 */
public class ImageProcessingUtil {

    static interface TlenCallback {
        public void tlen(String imageUrl);
    }

    private static final Pattern imagePattern = Pattern.compile("href=\"(.*)\"");

    public static void tlenifyImage(final int bgNum, final String imageUrl, final TlenCallback callback) {

        new Thread() {

            public void run() {

                byte[] image = HttpRequest.get(imageUrl).bytes();

                HttpRequest request = HttpRequest.post("http://tlenta.ru/tlenify.php");
                request.part("MAX_FILE_SIZE", "2000000");

                InputStream is = new ByteArrayInputStream(image);
                request.part("img", "foobar.jpg", "image/jpeg", is);
                request.part("fon", bgNum);
                request.part("txt", "");
                if (request.ok()) {
                    String body = request.body();
                    Matcher matcher = imagePattern.matcher(body);

                    if (matcher.find()) {
                        String url = matcher.group(1);
                        System.out.println(url);
                        callback.tlen(url);
                    }

                }
            }
        }.start();
    }
}
