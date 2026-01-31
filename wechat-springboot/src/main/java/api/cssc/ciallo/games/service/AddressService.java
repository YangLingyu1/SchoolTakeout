package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Address;
import java.util.List;

public interface AddressService {
    Address createAddress(Address address);
    Address getAddressById(Integer id);
    List<Address> getAddressesByUserId(Integer userId);
    Address getDefaultAddressByUserId(Integer userId);
    Address updateAddress(Address address);
    void deleteAddress(Integer id);
    void setDefaultAddress(Integer addressId, Integer userId);
}
