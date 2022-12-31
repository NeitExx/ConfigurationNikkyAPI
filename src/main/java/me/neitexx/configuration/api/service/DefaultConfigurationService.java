package me.neitexx.configuration.api.service;

import lombok.*;
import me.neitexx.configuration.api.repository.ConfigurationRepository;
import me.neitexx.configuration.api.repository.DefaultConfigurationRepository;
import me.neitexx.configuration.api.DefaultConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public final class DefaultConfigurationService implements ConfigurationService {

    @Getter(value = AccessLevel.PRIVATE)
    private ConfigurationRepository repository = new DefaultConfigurationRepository();

    @Override
    public <T extends DefaultConfiguration> Optional<T> findById(@NotNull final String id) {
        return getRepository().findById(id);
    }

    @Override
    public <T extends DefaultConfiguration> Optional<T> findByClass(@NotNull final Class<T> tClass) {
        return getRepository().findByClass(tClass);
    }

    public ConfigurationService initialize(@NotNull final JavaPlugin javaPlugin) {
        return this.initialize(javaPlugin, null);
    }

    @Override
    public ConfigurationService initialize(@NotNull final JavaPlugin javaPlugin, @Nullable final ConfigurationRepository configurationRepository) {
        if (configurationRepository != null) this.repository = configurationRepository;

        if (!getRepository().isDataFolderNull())
            throw new RuntimeException(String.format("Configuration repository for <%s> already initialized", javaPlugin.getClass().getSimpleName()));

        getRepository().setDataFolder(javaPlugin.getDataFolder());
        return this;
    }

    @Override
    @SafeVarargs
    public final ConfigurationService register(@NotNull final Class<? extends DefaultConfiguration>... configurationClasses) {
        for (Class<? extends DefaultConfiguration> configuration : configurationClasses)
            getRepository().append(configuration);

        return this;
    }

    @Override
    public ConfigurationService unregister(@NotNull final String... keys) {
        for (String key : keys)
            getRepository().invalidate(key);

        return this;
    }

    @Override
    public ConfigurationService unregisterAll() {
        this.repository.invalidateAll();
        return this;
    }

}
