package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.service.UserService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RiderService riderService;

    @Autowired
    private UserService userService;

    @PostMapping("/payment-code")
    public Response<?> uploadPaymentCode(@RequestParam("file") MultipartFile file, @RequestParam Integer userId) {
        if (file.isEmpty()) {
            return Response.badRequest("文件不能为空");
        }

        try {
            // 使用与WebMvcConfig相同的路径解析方式
            String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
            System.out.println("上传文件保存路径: " + absolutePath);
            
            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            File dest = new File(absolutePath + File.separator + "payment-codes" + File.separator + fileName);
            
            System.out.println("目标文件路径: " + dest.getAbsolutePath());
            
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            
            file.transferTo(dest);
            
            // 验证文件是否成功保存
            if (!dest.exists()) {
                throw new IOException("文件保存失败，目标文件不存在");
            }
            System.out.println("文件保存成功: " + dest.getAbsolutePath());
            
            String fileUrl = "/uploads/payment-codes/" + fileName;
            
            // 只有文件保存成功后才更新数据库
            riderService.updatePaymentCode(userId, fileUrl);
            System.out.println("数据库更新成功: " + fileUrl);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            
            return Response.success(result);
        } catch (IOException e) {
            System.out.println("上传失败: " + e.getMessage());
            e.printStackTrace();
            return Response.badRequest("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public Response<?> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam Integer userId) {
        if (file.isEmpty()) {
            return Response.badRequest("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        
        if (!isValidImageExtension(fileExtension)) {
            return Response.badRequest("只支持 jpg、jpeg、png、gif 格式的图片");
        }

        try {
            String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
            System.out.println("上传头像保存路径: " + absolutePath);
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            File dest = new File(absolutePath + File.separator + "avatars" + File.separator + fileName);
            
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            
            file.transferTo(dest);
            
            String fileUrl = "/uploads/avatars/" + fileName;
            
            // 更新用户头像
            userService.updateAvatar(userId, fileUrl);
            System.out.println("用户头像更新成功: " + fileUrl);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            
            return Response.success(result);
        } catch (IOException e) {
            return Response.badRequest("上传失败：" + e.getMessage());
        }
    }

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
            // 使用与WebMvcConfig相同的路径解析方式
            String absolutePath = Paths.get(uploadPath).toAbsolutePath().toString();
            System.out.println("上传文件保存路径: " + absolutePath);
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
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
