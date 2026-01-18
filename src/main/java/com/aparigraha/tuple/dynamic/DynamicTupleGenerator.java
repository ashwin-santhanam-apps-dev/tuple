package com.aparigraha.tuple.dynamic;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


public class DynamicTupleGenerator {
    private static final PebbleTemplate template = getTemplate();

    public String generate() throws IOException {
        Writer writer = new StringWriter();
        template.evaluate(writer);
        return writer.toString();
    }

    private static PebbleTemplate getTemplate() {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("templates");
        PebbleEngine engine = new PebbleEngine.Builder()
                .strictVariables(true)
                .autoEscaping(false)
                .loader(loader)
                .build();
        return engine.getTemplate("DynamicTuple.peb");
    }
}
