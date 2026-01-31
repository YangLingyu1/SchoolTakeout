package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Address;
import api.cssc.ciallo.games.service.AddressService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 获取用户地址列表
    @GetMapping
    public Response<?> getAddresses(@RequestParam Integer userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return Response.success(addresses);
    }

    // 获取默认地址
    @GetMapping("/default")
    public Response<?> getDefaultAddress(@RequestParam Integer userId) {
        Address address = addressService.getDefaultAddressByUserId(userId);
        return Response.success(address);
    }

    // 添加地址
    @PostMapping
    public Response<?> addAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return Response.success(createdAddress);
    }

    // 更新地址
    @PutMapping
    public Response<?> updateAddress(@RequestBody Address address) {
        Address updatedAddress = addressService.updateAddress(address);
        return Response.success(updatedAddress);
    }

    // 删除地址
    @DeleteMapping
    public Response<?> deleteAddress(@RequestParam Integer id) {
        addressService.deleteAddress(id);
        return Response.success(null);
    }

    // 设置默认地址
    @PutMapping("/default")
    public Response<?> setDefaultAddress(@RequestBody Map<String, Integer> request) {
        Integer addressId = request.get("addressId");
        Integer userId = request.get("userId");
        if (addressId == null || userId == null) {
            return Response.badRequest("参数错误");
        }
        addressService.setDefaultAddress(addressId, userId);
        return Response.success(null);
    }
}
