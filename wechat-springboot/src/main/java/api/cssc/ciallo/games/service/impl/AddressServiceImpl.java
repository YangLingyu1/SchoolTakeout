package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Address;
import api.cssc.ciallo.games.repository.AddressRepository;
import api.cssc.ciallo.games.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public Address createAddress(Address address) {
        // 如果是默认地址，先将其他地址设置为非默认
        if (address.getIsDefault()) {
            setDefaultAddressForUser(address.getUserId());
        }
        return addressRepository.save(address);
    }

    @Override
    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public List<Address> getAddressesByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address getDefaultAddressByUserId(Integer userId) {
        return addressRepository.findByUserIdAndIsDefault(userId, true);
    }

    @Override
    @Transactional
    public Address updateAddress(Address address) {
        // 如果是默认地址，先将其他地址设置为非默认
        if (address.getIsDefault()) {
            setDefaultAddressForUser(address.getUserId());
        }
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Integer addressId, Integer userId) {
        // 先将所有地址设置为非默认
        setDefaultAddressForUser(userId);
        // 再将指定地址设置为默认
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(true);
            addressRepository.save(address);
        }
    }

    // 辅助方法：将用户的所有地址设置为非默认
    @Transactional
    private void setDefaultAddressForUser(Integer userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address address : addresses) {
            address.setIsDefault(false);
        }
        // 批量保存所有地址
        addressRepository.saveAll(addresses);
    }
}
