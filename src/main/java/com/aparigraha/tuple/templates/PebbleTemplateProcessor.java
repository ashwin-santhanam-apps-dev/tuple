package com.aparigraha.tuple.templates;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PebbleTemplateProcessor {
    private final PebbleEngine engine;

    public PebbleTemplateProcessor(String templatePrefix) {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix(templatePrefix);
        engine = new PebbleEngine.Builder()
                .strictVariables(true)
                .autoEscaping(false)
                .loader(loader)
                .build();
    }


    public String process(String templateName, Map<String, Object> context) throws IOException {
        Writer writer = new StringWriter();
        engine.getTemplate(templateName).evaluate(writer, context);
        return writer.toString();
    }
}
