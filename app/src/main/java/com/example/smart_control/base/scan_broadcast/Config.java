package com.example.smart_control.base.scan_broadcast;

import java.util.regex.Pattern;

public final class Config {
    private static final boolean VERBOSE = false;

    /*
     *   @see {http://files.dns-sd.org/draft-cheshire-dnsext-dns-sd.txt}
     */
    public static final String SERVICES_DOMAIN = "_services._dns-sd._udp";

    public static final String EMPTY_DOMAIN = ".";
    public static final String LOCAL_DOMAIN = "local.";
    public static final String TCP_REG_TYPE_SUFFIX = "_tcp";
    public static final String UDP_REG_TYPE_SUFFIX = "_udp";

    public static final String REG_TYPE_SEPARATOR = Pattern.quote(".");
}
