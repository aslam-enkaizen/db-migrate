package com.exrade;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Rhidoy
 * @created 6/22/22
 */
public class Request {
    public Body body() {
        return new Body();
    }

    public class Body {
        public JsonNode asJson() {
            return null;
        }
    }
}
