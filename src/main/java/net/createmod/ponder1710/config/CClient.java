package net.createmod.ponder1710.config;

// ConfigBase from catnip not available in 1.7.10 - using plain fields
// Values can be persisted via Forge config system separately if needed
public class CClient {

    public boolean comfyReading = false;
    public boolean editingMode  = false;

    public enum PlacementIndicatorSetting {
        TEXTURE, TRIANGLE, NONE
    }

    public PlacementIndicatorSetting placementIndicator = PlacementIndicatorSetting.TEXTURE;
    public float indicatorScale = 1.0f;

    public String getName() {
        return "client";
    }
}
