package com.voicelk.voicelk_be.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpTest {

    @Test
    void dropsThePortAProxyAppends() {
        assertEquals("203.0.113.7", ClientIp.withoutPort("203.0.113.7:51234"));
        assertEquals("2001:db8::1", ClientIp.withoutPort("[2001:db8::1]:51234"));
    }

    @Test
    void leavesPlainAddressesAlone() {
        assertEquals("203.0.113.7", ClientIp.withoutPort("203.0.113.7"));
        assertEquals("2001:db8::1", ClientIp.withoutPort("2001:db8::1"));
    }

    @Test
    void ignoresAForwardedForHeaderTheClientSentItself() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.7");
        request.addHeader("X-Forwarded-For", "1.1.1.1");

        assertEquals("203.0.113.7", ClientIp.of(request));
    }
}
