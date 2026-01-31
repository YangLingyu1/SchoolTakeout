package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Dormitory;
import java.util.List;

public interface DormitoryService {
    Dormitory getDormitoryById(Integer id);
    List<Dormitory> getDormitories();
    List<Dormitory> getDormitoriesByNightDelivery(Boolean nightDelivery);
    Dormitory createDormitory(Dormitory dormitory);
    Dormitory updateDormitory(Dormitory dormitory);
    void deleteDormitory(Integer id);
}