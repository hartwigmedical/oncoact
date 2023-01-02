package com.hartwig.oncoact.patientreporter.xml;

import com.hartwig.oncoact.xml.KeyXML;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class SignatureXML {

    public abstract KeyXML msscore();

    @NotNull
    public abstract KeyXML msstatus();

    public abstract KeyXML tumuload();

    @NotNull
    public abstract KeyXML tumulosta();

    public abstract KeyXML tutmb();

    public abstract KeyXML horesco();

    @NotNull
    public abstract KeyXML horestu();

    @NotNull
    public abstract KeyXML geenpv();
}