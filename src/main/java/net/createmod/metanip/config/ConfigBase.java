package net.createmod.metanip.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

// Removed: NeoForge ModConfigSpec (not available in 1.7.10)
// Replaced with plain fields + Forge Configuration
// In 1.7.10 config is handled via net.minecraftforge.common.config.Configuration

public abstract class ConfigBase {

    protected List<CValue<?>> allValues = new ArrayList<>();
    protected List<ConfigBase> children = new ArrayList<>();

    public abstract String getName();

    public void onLoad() {
        children.forEach(ConfigBase::onLoad);
    }

    public void onReload() {
        children.forEach(ConfigBase::onReload);
    }

    // Simple typed config value wrapper - no ModConfigSpec needed
    public class CValue<V> {
        protected V value;
        protected final String name;
        protected final String[] comment;

        public CValue(String name, V defaultValue, String... comment) {
            this.name = name;
            this.value = defaultValue;
            this.comment = comment;
            allValues.add(this);
        }

        public V get() { return value; }

        public void set(V value) { this.value = value; }

        public String getName() { return name; }
    }

    public class ConfigBool extends CValue<Boolean> {
        public ConfigBool(String name, boolean def, String... comment) {
            super(name, def, comment);
        }
    }

    public class ConfigInt extends CValue<Integer> {
        public final int min, max;
        public ConfigInt(String name, int current, int min, int max, String... comment) {
            super(name, current, comment);
            this.min = min;
            this.max = max;
        }
    }

    public class ConfigFloat extends CValue<Float> {
        public final float min, max;
        public ConfigFloat(String name, float current, float min, float max, String... comment) {
            super(name, current, comment);
            this.min = min;
            this.max = max;
        }
        public float getF() { return value; }
    }

    public class ConfigEnum<T extends Enum<T>> extends CValue<T> {
        public ConfigEnum(String name, T defaultValue, String... comment) {
            super(name, defaultValue, comment);
        }
    }

    protected ConfigBool b(boolean current, String name, String... comment) {
        return new ConfigBool(name, current, comment);
    }

    protected ConfigFloat f(float current, float min, float max, String name, String... comment) {
        return new ConfigFloat(name, current, min, max, comment);
    }

    protected ConfigFloat f(float current, float min, String name, String... comment) {
        return f(current, min, Float.MAX_VALUE, name, comment);
    }

    protected ConfigInt i(int current, int min, int max, String name, String... comment) {
        return new ConfigInt(name, current, min, max, comment);
    }

    protected ConfigInt i(int current, int min, String name, String... comment) {
        return i(current, min, Integer.MAX_VALUE, name, comment);
    }

    protected ConfigInt i(int current, String name, String... comment) {
        return i(current, Integer.MIN_VALUE, Integer.MAX_VALUE, name, comment);
    }

    protected <T extends Enum<T>> ConfigEnum<T> e(T defaultValue, String name, String... comment) {
        return new ConfigEnum<>(name, defaultValue, comment);
    }
}
