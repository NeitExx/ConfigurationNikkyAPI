package me.neitexx.configuration.api;

import me.neitexx.configuration.api.reflect.FieldHandler;
import me.neitexx.configuration.api.reflect.ReflectHandler;

import java.io.File;

public interface Configuration {

    File getFile();

    ReflectHandler getReflectHandler();

    FieldHandler getFieldHandler();

    String getConfigurationName();

    default String getDirectory() {
        return "";
    }

}