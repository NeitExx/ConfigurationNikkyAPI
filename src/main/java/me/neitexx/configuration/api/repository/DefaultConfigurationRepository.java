package me.neitexx.configuration.api.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.neitexx.configuration.api.DefaultConfiguration;
import me.neitexx.configuration.api.annotation.ConfigurationKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultConfigurationRepository implements ConfigurationRepository {

    private final Map<String, DefaultConfiguration> configs = new HashMap<>();
    private File dataFolder;

    @Override
    public void setDataFolder(@NotNull final File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean isDataFolderNull() {
        return dataFolder == null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DefaultConfiguration> Optional<T> findById(@NotNull final String id) {
        val value = configs.get(id);
        return Optional.ofNullable(value == null ? null : (T) value);
    }

    @Override
    public <T extends DefaultConfiguration> Optional<T> findByClass(@NotNull final Class<T> tClass) {
        return this.findById(tClass.getSimpleName());
    }

    @Override
    public List<DefaultConfiguration> findAll() {
        return new ArrayList<>(configs.values());
    }

    @Override
    public void save(@NotNull final String key) {
        this.findById(key).ifPresent(DefaultConfiguration::save);
    }

    @Override
    public void save(@NotNull final Class<? extends DefaultConfiguration> tClass) {
        this.findByClass(tClass).ifPresent(DefaultConfiguration::save);
    }

    @Override
    public void saveAll() {
        this.findAll().forEach(DefaultConfiguration::save);
    }

    @Override
    public boolean append(@NotNull final Class<? extends DefaultConfiguration> configurationClass) {
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

    @Override
    public void invalidate(@NotNull final String key){
        this.configs.remove(key);
    }

    @Override
    public void invalidate(@NotNull final Class<? extends DefaultConfiguration> tClass) {
        this.configs.remove(tClass.getSimpleName());
    }

    @Override
    public void invalidateAll() {

    }

}
