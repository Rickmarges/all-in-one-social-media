package com.dash.Utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenericParserTest {

    @Test
    public void isValidUrl() {
        // Test protocol
        assertEquals(GenericParser.isValidUrl("test.com"), false);
        assertEquals(GenericParser.isValidUrl("www.test.com"), false);
        assertEquals(GenericParser.isValidUrl("http://www.test.com"), false);
        assertEquals(GenericParser.isValidUrl("https:www.test.com"), false);
        assertEquals(GenericParser.isValidUrl("https://test.com"), true);
        assertEquals(GenericParser.isValidUrl("https://www.test.com"), true);

        // Test special characters
        assertEquals(GenericParser.isValidUrl("https://www.te<tag>st.com"), false);
        assertEquals(GenericParser.isValidUrl("https://www.te()st.com"), false);
        assertEquals(GenericParser.isValidUrl("https://www.te'+iets+'st.com"), false);
        assertEquals(GenericParser.isValidUrl("https://www.te-iets-st.co.uk"), true);

    }
}