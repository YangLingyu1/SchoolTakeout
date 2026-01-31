package api.cssc.ciallo.games.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
        System.out.println("静态资源映射路径: " + absolutePath);
        
        // 在Windows上，file:协议需要使用正斜杠
        String resourceLocation = "file:/" + absolutePath.replace("\\", "/");
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }
        System.out.println("资源位置: " + resourceLocation);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
