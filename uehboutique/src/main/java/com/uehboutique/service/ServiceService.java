package com.uehboutique.service;

import com.uehboutique.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    // 1. Lấy danh sách tất cả dịch vụ (Menu)
    public List<com.uehboutique.entity.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // 2. Thêm dịch vụ mới (VD: Bò húc)
    public com.uehboutique.entity.Service addService(com.uehboutique.entity.Service service) {
        return serviceRepository.save(service);
    }

    // 3. Sửa dịch vụ (VD: Đổi giá Bò húc từ 20k lên 25k)
    public com.uehboutique.entity.Service updateService(Integer id, com.uehboutique.entity.Service serviceDetails) {
        com.uehboutique.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can not find service with id: " + id));

        service.setServiceName(serviceDetails.getServiceName());
        service.setUnitPrice(serviceDetails.getUnitPrice());

        return serviceRepository.save(service);
    }

    // 4. Xóa dịch vụ khỏi Menu
    public void deleteService(Integer id) {
        serviceRepository.deleteById(id);
    }
}
