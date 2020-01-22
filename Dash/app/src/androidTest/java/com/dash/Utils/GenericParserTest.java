package com.dash.Utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericParserTest {

    @Test
    public void isValidUrl() {
        // Test protocol
        assertFalse(GenericParser.isValidUrl("test.com"));
        assertFalse(GenericParser.isValidUrl("www.test.com"));
        assertFalse(GenericParser.isValidUrl("http://www.test.com"));
        assertFalse(GenericParser.isValidUrl("https:www.test.com"));
        assertTrue(GenericParser.isValidUrl("https://test.com"));
        assertTrue(GenericParser.isValidUrl("https://www.test.com"));

        // Test special characters
        assertFalse(GenericParser.isValidUrl("https://www.te<tag>st.com"));
        assertFalse(GenericParser.isValidUrl("https://www.te()st.com"));
        assertFalse(GenericParser.isValidUrl("https://www.te'+iets+'st.com"));
        assertTrue(GenericParser.isValidUrl("https://www.te-iets-st.co.uk"));

    }

    @Test
    public void isSecureUrl() {
        // Test correct connection
        assertTrue(GenericParser.isSecureUrl("https://www.test.com"));
        assertFalse(GenericParser.isSecureUrl("http://www.test.com"));
        assertFalse(GenericParser.isSecureUrl("www.test.com"));
        // This URLs certificate's match and therefore 'secure' (http is filtered before this check)
        assertFalse(GenericParser.isSecureUrl("http://www.verkeersschoolnijland.nl/"));

        // Test a domain without https using https in the url
        assertFalse(GenericParser.isSecureUrl("https://www.verkeersschoolnijland.nl/"));
        assertFalse(GenericParser.isSecureUrl("www.verkeersschoolnijland.nl"));
    }
}