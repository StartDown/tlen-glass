package com.google.glassware;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yarik on 7/27/13.
 */
public class ImageProcessingUtil {

    private static final Pattern imagePattern = Pattern.compile("href=\"(.*)\"");
    private static final Random random = new Random();

    public static String tlenifyImage(final InputStream is) {

        final int bgNum = random.nextInt(3)+1;

        HttpRequest request = HttpRequest.post("http://tlenta.ru/tlenify.php");
        request.part("MAX_FILE_SIZE", "2000000");

        request.part("img", "foobar.jpg", "image/jpeg", is);
        request.part("fon", bgNum);
        request.part("txt", "");
        if (request.ok()) {
            String body = request.body();
            Matcher matcher = imagePattern.matcher(body);

            if (matcher.find()) {
                String url = matcher.group(1);
                System.out.println(url);
                return url;

            }

        }

        return null;
    }
}
