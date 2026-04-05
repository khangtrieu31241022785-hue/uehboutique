package com.uehboutique.service;

import com.uehboutique.entity.Invoice;
import com.uehboutique.entity.Room;
import com.uehboutique.repository.InvoiceRepository;
import com.uehboutique.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;


    public Map<String, Object> getDashboardStats(){
        Map<String, Object> stats = new HashMap<>();

        // 1. TÍNH TỔNG DOANH THU HÔM NAY
        LocalDate today = LocalDate.now();
        List<Invoice> allInvoices = invoiceRepository.findAll();

        java.math.BigDecimal todayRevenue = allInvoices.stream()
                .filter(inv -> inv.getPaymentDate() != null && inv.getPaymentDate().toLocalDate().equals(today))
                .map(Invoice::getTotalAmount) // Lấy ra danh sách các cục BigDecimal
                .filter(java.util.Objects::nonNull) // Loại bỏ invoice nào bị null tiền (cho chắc)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add); // Cộng dồn lại
        stats.put("todayRevenue", todayRevenue);

        // 2. THỐNG KÊ TRẠNG THÁI PHÒNG
        java.util.List<Room> allRooms = roomRepository.findAll();
        java.util.Map<String, Long> roomStatusCount = allRooms.stream()
                .collect(java.util.stream.Collectors.groupingBy(Room::getStatus, java.util.stream.Collectors.counting()));

        stats.put("roomStatistics", roomStatusCount);
        //Tổng số phòng của KS
        stats.put("totalRooms", allRooms.size());

        return stats;
    }
}
