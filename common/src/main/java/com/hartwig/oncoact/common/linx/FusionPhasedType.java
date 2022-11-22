package com.hartwig.oncoact.common.linx;

public enum FusionPhasedType
{
    INFRAME,
    SKIPPED_EXONS,
    OUT_OF_FRAME;

    public String displayStr()
    {
        switch(this)
        {
            case INFRAME: return "Inframe";
            case SKIPPED_EXONS: return "Skipped exons";
            case OUT_OF_FRAME: return "Out of frame";
            default: return "Invalid";
        }
    }
}
