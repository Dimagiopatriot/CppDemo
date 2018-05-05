package com.cipherme.api.headers;

public class HeadersProvider {

    public enum Key implements ValueProvider<String> {

        VERSION("Version"),
        ACCEPT_LANGUAGE("Accept-Language"),
        CONTENT_TYPE("Content-Type"),
        AUTH_TOKEN("Auth-Token");

        private final String value;

        Key(final String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
