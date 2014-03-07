package com.en_workshop.webcrawlerakka.tools;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Radu Ciumag
 */
public class WebContentToolsTest {

    @Test
    public void testNormalizeURLLink_lowerCase() throws Exception {
        /* Converting the scheme and host to lower case. The scheme and host components of the URL are case-insensitive.
            HTTP://www.Example.com/ → http://www.example.com/ */
        final String inLink = "HTTP://www.Example.com/";
        final String outLink = "http://www.example.com/";

        assertEquals("Lower case conversion failed", inLink, WebContentTools.normalizeURLLink(inLink));
    }

    @Test
    public void testNormalizeURLLink() throws Exception {
        /* Decoding percent-encoded octets of unreserved characters. For consistency, percent-encoded octets in the ranges of ALPHA (%41–%5A and %61–%7A), DIGIT (%30–%39),
        hyphen (%2D), period (%2E), underscore (%5F), or tilde (%7E) should not be created by URI producers and, when found in a URI, should be decoded to their
        corresponding unreserved characters by URI normalizers.
            http://www.example.com/%7Eusername/ → http://www.example.com/~username/ */
    }

    @Test
    public void testNormalizeURLLink() throws Exception {
        /* Removing the default port. The default port (port 80 for the “http” scheme) may be removed from (or added to) a URL.
            http://www.example.com:80/bar.html → http://www.example.com/bar.html */
    }

        /* Removing dot-segments. The segments “..” and “.” can be removed from a URL according to the algorithm described in RFC 3986 (or a similar algorithm).
            http://www.example.com/../a/b/../c/./d.html → http://www.example.com/a/c/d.html
        However, if a removed ".." component, e.g. "b/..", is a symlink to a directory with a different parent, eliding "b/.." will result in a different path and URL. */

        /* Limiting protocols. Limiting different application layer protocols. For example, the “https” scheme could be replaced with “http”.
            https://www.example.com/ → http://www.example.com/ */

        /* Removing duplicate slashes Paths which include two adjacent slashes could be converted to one.
            http://www.example.com/foo//bar.html → http://www.example.com/foo/bar.html */

        /* Removing or adding “www” as the first domain label. Some websites operate in two Internet domains: one whose least significant label is “www” and another whose name
        is the result of omitting the least significant label from the name of the first. For example, http://example.com/ and http://www.example.com/ may access the same
        website. Many websites redirect the user from the www to the non-www address or vice versa. A normalizer may determine if one of these URLs redirects to the other
        and normalize all URLs appropriately.
            http://www.example.com/ → http://example.com/ */

        /* Sorting the query parameters. Some web pages use more than one query parameter in the URL. A normalizer can sort the parameters into alphabetical order (with their
        values), and reassemble the URL.
            http://www.example.com/display?lang=en&article=fred → http://www.example.com/display?article=fred&lang=en
        However, the order of parameters in a URL may be significant (this is not defined by the standard) and a web server may allow the same variable to appear multiple
        times. */

        /* Removing unused query variables. A page may only expect certain parameters to appear in the query; unused parameters can be removed.
            http://www.example.com/display?id=123&fakefoo=fakebar → http://www.example.com/display?id=123
        Note that a parameter without a value is not necessarily an unused parameter. */

        /* Removing the "?" when the query is empty. When the query is empty, there may be no need for the "?".
            http://www.example.com/display? → http://www.example.com/display */
}
