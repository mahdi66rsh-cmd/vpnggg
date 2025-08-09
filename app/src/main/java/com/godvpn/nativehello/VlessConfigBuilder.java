package com.godvpn.nativehello;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class VlessConfigBuilder {
    public static String fromLink(String vless) {
        // مقادیر ثابت از لینک شما:
        String uuid = "e3075864-27df-4d15-b181-9c94b0e6a53c";
        String address = "216.9.224.59";
        int port = 443;
        String sni = "onlinebazikon.ir";
        String host = "onlinebazikon.ir";
        String path = decode("%2FvTlFuPRGGIPbezDZVgy1yX");

        return "{\n" +
                "  \"log\": {\"loglevel\": \"warning\"},\n" +
                "  \"inbounds\": [\n" +
                "    {\"tag\":\"socks-in\",\"port\":10808,\"listen\":\"127.0.0.1\",\"protocol\":\"socks\",\"settings\":{\"udp\":true}}\n" +
                "  ],\n" +
                "  \"outbounds\": [\n" +
                "    {\"protocol\":\"vless\",\"settings\":{\n" +
                "       \"vnext\":[{\"address\":\""+address+"\",\"port\":"+port+",\"users\":[{\"id\":\""+uuid+"\",\"encryption\":\"none\",\"flow\":null}]}]\n" +
                "     },\n" +
                "     \"streamSettings\":{ \"network\":\"httpupgrade\",\n" +
                "       \"security\":\"tls\", \"tlsSettings\":{\"serverName\":\""+sni+"\",\"allowInsecure\":false},\n" +
                "       \"httpupgradeSettings\":{\"path\":\""+path+"\",\"host\":\""+host+"\"},\n" +
                "       \"sockopt\":{\"dialerProxy\":null}\n" +
                "     }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
    }
    private static String decode(String s){ return URLDecoder.decode(s, StandardCharsets.UTF_8); }
}
