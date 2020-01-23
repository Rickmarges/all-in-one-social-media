package com.dash.Utils;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
public class GenericParserTest {

    @Test
    public void isValidUrl() {
        // Test protocol
        assertNull(GenericParser.isValidUrl("test.com"));
        assertNull(GenericParser.isValidUrl("www.test.com"));
        assertNull(GenericParser.isValidUrl("http://www.test.com"));
        assertNull(GenericParser.isValidUrl("https:www.test.com"));
        assertNotNull(GenericParser.isValidUrl("https://test.com"));
        assertNotNull(GenericParser.isValidUrl("https://www.test.com"));

        // Test special characters
        assertNull(GenericParser.isValidUrl("https://www.te<tag>st.com"));
        assertNull(GenericParser.isValidUrl("https://www.te()st.com"));
        assertNull(GenericParser.isValidUrl("https://www.te'+iets+'st.com"));
        assertNotNull(GenericParser.isValidUrl("https://www.te-iets-st.co.uk"));

    }

    @Test
    public void isSecureUrl() {
        // Test correct connection
        assertNotNull(GenericParser.isSecureUrl("https://www.test.com"));
        assertNull(GenericParser.isSecureUrl("http://www.test.com"));
        assertNull(GenericParser.isSecureUrl("www.test.com"));
        // This URLs certificate's match and therefore 'secure' (http is filtered before this check)
        assertNull(GenericParser.isSecureUrl("http://www.verkeersschoolnijland.nl/"));

        // Test a domain without https using https in the url
        assertNull(GenericParser.isSecureUrl("https://www.verkeersschoolnijland.nl/"));
        assertNull(GenericParser.isSecureUrl("www.verkeersschoolnijland.nl"));
    }
}