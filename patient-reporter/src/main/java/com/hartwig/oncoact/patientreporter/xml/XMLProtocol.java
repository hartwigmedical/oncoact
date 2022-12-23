package com.hartwig.oncoact.patientreporter.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class XMLProtocol {

    @JacksonXmlProperty(localName = "meta")
    @NotNull
    public abstract protocolNameXML meta();

    @JacksonXmlProperty(localName = "content")
    @NotNull
    public abstract ContentXML content();
}