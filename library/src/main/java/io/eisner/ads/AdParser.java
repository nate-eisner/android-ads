package io.eisner.ads;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Parses XML to get image urls
 * Created by nathan eisner
 */
public class AdParser {

    /**
     * Parses XML file to get ad uris
     *
     * @param inputStream the xml response from server
     * @return ArrayList of ad uris
     */
    public static ArrayList<String> parseForImages(InputStream inputStream, String aTagName) {
        return parse(inputStream, aTagName);
    }

    /**
     * Used to find the API key in a response in the tags <item>KEY HERE</item>
     *
     * @param inputStream the response body as byte stream
     * @return found key or empty if no key
     */
    public static String parseForKey(InputStream inputStream) {
        return parse(inputStream, "item").get(0);
    }

    /**
     * Internal parse that uses XMLPullParser to find text located inside of tagToFind
     *
     * @param inputStream the XML file that is being parsed
     * @param tagToFind   the tag that the parser will look for
     * @return an ArrayList of Strings found within the given tag name
     */
    private static ArrayList<String> parse(InputStream inputStream, String tagToFind) {
        ArrayList<String> result = new ArrayList<>();
        XmlPullParserFactory factory;
        XmlPullParser parser;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (tagName != null && tagName.equalsIgnoreCase(tagToFind)) {
                    parser.next();
                    String text = parser.getText();
                    if (text != null && !text.isEmpty()) {
                        result.add(text);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
