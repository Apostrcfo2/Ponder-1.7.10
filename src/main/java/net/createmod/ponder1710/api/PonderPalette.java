package net.createmod.ponder1710.api;

// import net.createmod.catnip.theme.Color; // TODO: catnip not available in 1.7.10
// Replaced with plain int color values

public enum PonderPalette {

    WHITE(0xeeeeee),
    BLACK(0x221111),

    RED(0xff5d6c),
    GREEN(0x8cba51),
    BLUE(0x5f6caf),

    SLOW(0x22ff22),
    MEDIUM(0x0084ff),
    FAST(0xff55ff),

    INPUT(0x7fcde0),
    OUTPUT(0xddc166),

    ;

    private final int color;

    PonderPalette(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    // TODO: getColorObject() - Color from catnip not available in 1.7.10
    // public Color getColorObject() {
    //     return color;
    // }
}
