package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/product-image")
    public Response<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Response.badRequest("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        
        if (!isValidImageExtension(fileExtension)) {
            return Response.badRequest("只支持 jpg、jpeg、png、gif 格式的图片");
        }

        try {
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
            File dest = new File(absolutePath + File.separator + "products" + File.separator + fileName);
            
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            
            file.transferTo(dest);
            
            String fileUrl = "/uploads/products/" + fileName;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            
            return Response.success(result);
        } catch (IOException e) {
            return Response.badRequest("上传失败：" + e.getMessage());
        }
    }

    private boolean isValidImageExtension(String extension) {
        return extension.equals(".jpg") || extension.equals(".jpeg") || 
               extension.equals(".png") || extension.equals(".gif");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
