package io.github.kimmking.gateway.endpoint;

import java.util.ArrayList;
import java.util.List;

public class Endpoint {
    private final static String HOST = "http://127.0.0.1:";
    private final static String ENDPINT_1 = HOST + "8087";
    private final static String ENDPINT_2 = HOST + "8088";
    private final static String ENDPINT_3 = HOST + "8089";

    public final static String ENDPINT_DEFAULT = HOST + "8090";
    public final static List<String> ENDPOINTS = new ArrayList<>();
    static {
        ENDPOINTS.add(ENDPINT_1);
        ENDPOINTS.add(ENDPINT_2);
        ENDPOINTS.add(ENDPINT_3);
    }
}
