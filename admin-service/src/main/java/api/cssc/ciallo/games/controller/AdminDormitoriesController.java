package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Dormitory;
import api.cssc.ciallo.games.service.DormitoryService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dormitories")
public class AdminDormitoriesController {

    @Autowired
    private DormitoryService dormitoryService;

    @GetMapping
    public Response<?> getDormitories(@RequestParam(required = false) Boolean nightDelivery) {
        List<Dormitory> dormitories;
        if (nightDelivery != null) {
            dormitories = dormitoryService.getDormitoriesByNightDelivery(nightDelivery);
        } else {
            dormitories = dormitoryService.getDormitories();
        }
        return Response.success(dormitories);
    }

    @GetMapping("/{id}")
    public Response<?> getDormitoryById(@PathVariable Integer id) {
        Dormitory dormitory = dormitoryService.getDormitoryById(id);
        if (dormitory == null) {
            return Response.notFound("宿舍楼不存在");
        }
        return Response.success(dormitory);
    }

    @PostMapping
    public Response<?> createDormitory(@RequestBody Dormitory dormitory) {
        Dormitory createdDormitory = dormitoryService.createDormitory(dormitory);
        return Response.success(createdDormitory);
    }

    @PutMapping("/{id}")
    public Response<?> updateDormitory(@PathVariable Integer id, @RequestBody Dormitory dormitory) {
        dormitory.setId(id);
        Dormitory updatedDormitory = dormitoryService.updateDormitory(dormitory);
        return Response.success(updatedDormitory);
    }

    @DeleteMapping("/{id}")
    public Response<?> deleteDormitory(@PathVariable Integer id) {
        dormitoryService.deleteDormitory(id);
        return Response.success();
    }
}