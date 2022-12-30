package me.neitexx.configuration.api;

import lombok.*;
import me.neitexx.configuration.api.annotation.ConfigurationPath;
import me.neitexx.configuration.api.reflect.DefaultReflectHandler;
import me.neitexx.configuration.api.reflect.ReflectHandler;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public abstract class BasicConfiguration extends YamlConfiguration implements Configuration {

    @Getter private File file;
    @Getter @Setter(AccessLevel.PROTECTED) private ReflectHandler reflectHandler;

    public BasicConfiguration initialize(@NotNull final File dataFolder){
        if (getReflectHandler() == null) this.reflectHandler = new DefaultReflectHandler();

        if(getFile() != null) throw new RuntimeException(String.format("Configuration <%s> already initialized", this.getClass().getSimpleName()));
        this.createIfNotExist(dataFolder);

        if(getFile() == null) throw new RuntimeException("Error in configuration initializing");

        this.execute(FieldExecuteType.WRITE);
        this.afterInitializing();

        return this;
    }

    public abstract void afterInitializing();

    private void createIfNotExist(@NotNull final File dataFolder) {
        val directory = new File(dataFolder, this.getDirectory());
        val file = new File(directory, getConfigurationName());

        try {
            Files.createDirectory(directory.toPath());
            Files.createFile(file.toPath());

            this.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new RuntimeException(String.format("Some trouble with loading <%s> configuration class", this.getClass().getSimpleName()), exception);
        }

        this.file = file;
    }

    @SneakyThrows
    private void execute(@NotNull final FieldExecuteType executeType) {
        synchronized (this) {
            Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(ConfigurationPath.class))
                    .forEach(field -> {
                        val path = field.getAnnotation(ConfigurationPath.class).path();

                        getReflectHandler().executeAccessible(accessibleObject -> {
                            if (executeType == FieldExecuteType.WRITE)
                                getReflectHandler().setFieldValue(this, accessibleObject, this.get(path));
                            else if (executeType == FieldExecuteType.READ)
                                this.set(path, getReflectHandler().getFieldValue(this, accessibleObject));
                        }, field);
                    });
        }
    }

    public void save() {
        this.execute(FieldExecuteType.READ);

        try {
            this.save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
