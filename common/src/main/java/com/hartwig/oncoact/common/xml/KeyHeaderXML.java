package com.hartwig.oncoact.common.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class KeyHeaderXML {
    @JacksonXmlProperty(localName = "meta")
    @NotNull
    public abstract String keyMeta();
}