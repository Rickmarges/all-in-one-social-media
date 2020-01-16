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

    @Test
    public void isSecureUrl() {
        // Test correct connection
        assertEquals(GenericParser.isSecureUrl("https://www.test.com"), true);
        assertEquals(GenericParser.isSecureUrl("http://www.test.com"), true);
        assertEquals(GenericParser.isSecureUrl("www.test.com"), false);
        // This URLs certificate's match and therefore 'secure' (http is filtered before this check)
        assertEquals(GenericParser.isSecureUrl("http://www.verkeersschoolnijland.nl/"), true);

        // Test a domain without https using https in the url
        assertEquals(GenericParser.isSecureUrl("https://www.verkeersschoolnijland.nl/"), false);
        assertEquals(GenericParser.isSecureUrl("www.verkeersschoolnijland.nl"), false);
    }
}