package com.voicelk.voicelk_be.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The address a request came from, as used to recognise a returning guest.
 *
 * <p>Behind a proxy (Azure's ingress, or nginx on the same machine) the servlet container
 * resolves the real client from X-Forwarded-For, because
 * {@code server.forward-headers-strategy=native} trusts that header only on requests that
 * arrive from an internal proxy address. The header is not read here: its first entry is
 * whatever the client sent, so trusting it would let anyone choose their own address.
 *
 * <p>Some proxies, Azure App Service among them, record the client as {@code address:port}.
 * The port changes with every connection, so it is dropped.
 */
public final class ClientIp {

    private ClientIp() {
    }

    public static String of(HttpServletRequest request) {
        return withoutPort(request.getRemoteAddr());
    }

    static String withoutPort(String address) {
        if (address == null) {
            return null;
        }
        String trimmed = address.trim();
        // [IPv6]:port
        if (trimmed.startsWith("[")) {
            int end = trimmed.indexOf(']');
            return end > 0 ? trimmed.substring(1, end) : trimmed;
        }
        // IPv4:port. A bare IPv6 address has several colons and is left alone.
        int colon = trimmed.indexOf(':');
        if (colon > 0 && colon == trimmed.lastIndexOf(':')) {
            return trimmed.substring(0, colon);
        }
        return trimmed;
    }
}
