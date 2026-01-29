package pl.rentathing.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for customizing the behavior of the Spring MVC framework.
 * Implements {@link WebMvcConfigurer} to provide additional configuration options.
 * Specifically, this class configures resource handlers to serve static content
 * for files stored in a custom upload directory defined in the application properties.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Represents the directory path used for storing uploaded files.
     * The value is injected from the application properties using the key `app.upload.dir`.
     * This variable is used to configure the resource handler in order to
     * serve uploaded files as static resources.
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Configures resource handlers to serve static resources from a custom upload directory.
     * Maps the "/uploads/**" URL pattern to the physical directory specified by the `uploadDir` property.
     *
     * @param registry the ResourceHandlerRegistry that allows customization of resource handling
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        if (!uploadAbsolutePath.endsWith(java.io.File.separator)) {
            uploadAbsolutePath += java.io.File.separator;
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath);
    }
}