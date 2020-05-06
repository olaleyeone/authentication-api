package com.olaleyeone.auth.integration.etc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Map;

public class TemplateEngineImpl implements TemplateEngine {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final Configuration configuration;

    public TemplateEngineImpl() {
        configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    @Override
    public String getAsString(String templateStr, Map<String, Object> bindings) {
        return new String(getBytes(templateStr, bindings));
    }

    @SneakyThrows
    @Override
    public byte[] getBytes(String templateStr, Map<String, Object> bindings) {
        Template tpl = new Template(null, new StringReader(templateStr), configuration);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(bout);
        tpl.process(bindings, writer);
        return bout.toByteArray();
    }

}
