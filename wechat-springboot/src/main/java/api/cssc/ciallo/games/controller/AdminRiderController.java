package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.RiderApplication;
import api.cssc.ciallo.games.service.RiderApplicationService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/riders")
public class AdminRiderController {

    @Autowired
    private RiderApplicationService riderApplicationService;

    @GetMapping("/applications")
    public Response<?> getAllApplications() {
        List<RiderApplication> applications = riderApplicationService.getAllApplications();
        return Response.success(applications);
    }

    @GetMapping("/applications/{id}")
    public Response<?> getApplicationById(@PathVariable Integer id) {
        return riderApplicationService.getApplicationById(id)
                .map(Response::success)
                .orElse(Response.notFound("申请不存在"));
    }

    @PutMapping("/applications/{id}/review")
    public Response<?> reviewApplication(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String rejectReason = request.get("rejectReason");

            if (!"approved".equals(status) && !"rejected".equals(status)) {
                return Response.badRequest("无效的状态");
            }

            RiderApplication application = riderApplicationService.reviewApplication(id, status, rejectReason);
            return Response.success(application);
        } catch (Exception e) {
            return Response.internalError("审核失败：" + e.getMessage());
        }
    }
}
