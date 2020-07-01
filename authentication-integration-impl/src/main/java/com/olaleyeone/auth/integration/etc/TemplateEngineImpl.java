package com.olaleyeone.auth.integration.etc;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

@Component
public class TemplateEngineImpl implements TemplateEngine {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final Configuration configuration;
    private final TemplateLoader templateLoader;

    public TemplateEngineImpl() {
        configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templateLoader = new ClassTemplateLoader(getClass().getClassLoader(), "/templates/email/");
        configuration.setTemplateLoader(templateLoader);
    }

    @Override
    public String getAsString(String templateStr, Map<String, Object> bindings) {
        return new String(getBytes(templateStr, bindings));
    }

    @SneakyThrows
    @Override
    public byte[] getBytes(String template, Map<String, Object> bindings) {
        Template tpl = configuration.getTemplate(template);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(bout);
        tpl.process(bindings, writer);
        return bout.toByteArray();
    }

}
