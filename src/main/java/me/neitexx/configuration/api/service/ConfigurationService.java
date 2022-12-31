package me.neitexx.configuration.api.service;

import lombok.*;
import me.neitexx.configuration.api.repository.ConfigurationRepository;
import me.neitexx.configuration.api.DefaultConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationService {

    private final Map<JavaPlugin, ConfigurationRepository> repositories = new ConcurrentHashMap<>();

    @Getter
    private static final ConfigurationService instance = new ConfigurationService();

    public ConfigurationService appendPlugin(@NotNull final JavaPlugin javaPlugin) {
        if(this.repositories.containsKey(javaPlugin))
            throw new RuntimeException(String.format("Configuration repository for <%s> already initialized", javaPlugin.getClass().getSimpleName()));

        val repository = new ConfigurationRepository(javaPlugin.getDataFolder());
        this.repositories.put(javaPlugin, repository);

        return this;
    }

    @SafeVarargs
    public final ConfigurationService register(@NotNull final JavaPlugin javaPlugin,
                                               @NotNull final Class<? extends DefaultConfiguration>... configurationClasses){
        val repository = this.getRepository(javaPlugin);

        for (Class<? extends DefaultConfiguration> configuration : configurationClasses)
            repository.add(configuration);

        return this;
    }

    public ConfigurationService unregister(@NotNull final JavaPlugin javaPlugin, @NotNull final String... keys){
        val repository = this.getRepository(javaPlugin);

        for (String key : keys)
            repository.remove(key);

        return this;
    }

    public ConfigurationService unregister(@NotNull final JavaPlugin javaPlugin){
        this.repositories.remove(javaPlugin);
        return this;
    }

    public ConfigurationRepository getRepository(@NotNull final JavaPlugin javaPlugin){
        if (!this.repositories.containsKey(javaPlugin)) this.appendPlugin(javaPlugin);

        return this.repositories.get(javaPlugin);
    }

}
