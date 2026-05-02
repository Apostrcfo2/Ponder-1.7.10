package net.createmod.ponder1710.config;

// import net.createmod.catnip.config.ConfigBase; // TODO: catnip not available in 1.7.10
// Using simple boolean fields instead

public class CClient {

    public boolean comfyReading = false;
    public boolean editingMode = false;

    public enum PlacementIndicatorSetting {
        TEXTURE, TRIANGLE, NONE
    }

    public PlacementIndicatorSetting placementIndicator = PlacementIndicatorSetting.TEXTURE;
    public float indicatorScale = 1.0f;

    public String getName() {
        return "client";
    }
}
