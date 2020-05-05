package com.olaleyeone.auth.integration.etc;

import java.util.Map;

public interface TemplateEngine {

    String getAsString(String templateStr, Map<String, Object> bindings);

    byte[] getBytes(String templateStr, Map<String, Object> bindings);
}
