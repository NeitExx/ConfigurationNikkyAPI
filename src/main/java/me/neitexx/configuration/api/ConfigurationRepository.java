package me.neitexx.configuration.api;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.neitexx.configuration.api.annotation.ConfigurationKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class ConfigurationRepository {

    private final Map<String, BasicConfiguration> configs = new HashMap<>();
    private final File dataFolder;

    @SuppressWarnings("unchecked")
    public <T extends BasicConfiguration> Optional<T> findById(@NotNull final String id) {
        val value = configs.get(id);
        return Optional.ofNullable(value == null ? null : (T) value);
    }

    public void save(@NotNull final String key) {
        configs.get(key).save();
    }

    public void saveAll() {
        this.configs.values().forEach(BasicConfiguration::save);
    }

    public boolean add(@NotNull final Class<? extends BasicConfiguration> configurationClass){
        val configurationKey = configurationClass.isAnnotationPresent(ConfigurationKey.class) ?
                configurationClass.getDeclaredAnnotation(ConfigurationKey.class).key() : configurationClass.getSimpleName();

        try {
            this.configs.put(configurationKey, configurationClass.newInstance().initialize(dataFolder));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void remove(@NotNull final String key){
        this.configs.remove(key);
    }

}
