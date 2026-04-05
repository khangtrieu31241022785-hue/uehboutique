package com.uehboutique.service;

import com.uehboutique.entity.Invoice;
import com.uehboutique.entity.Room;
import com.uehboutique.repository.InvoiceRepository;
import com.uehboutique.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetDashboardStats_Success() {
        // 1. CHUẨN BỊ MOCK DATA
        // Dùng LocalDate để khớp y chang với biến 'today' trong DashboardService của bác
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // --- Data Hóa đơn ---
        Invoice inv1 = new Invoice();
        inv1.setPaymentDate(today.atStartOfDay()); // Doanh thu hôm nay
        inv1.setTotalAmount(new BigDecimal("1000000"));

        Invoice inv2 = new Invoice();
        inv2.setPaymentDate(today.atStartOfDay()); // Doanh thu hôm nay
        inv2.setTotalAmount(new BigDecimal("500000"));

        Invoice inv3 = new Invoice();
        inv3.setPaymentDate(yesterday.atStartOfDay()); // Hóa đơn hôm qua -> Phải bị loại
        inv3.setTotalAmount(new BigDecimal("2000000"));

        Invoice inv4 = new Invoice();
        inv4.setPaymentDate(null); // Không có ngày -> Phải bị loại, không được lỗi
        inv4.setTotalAmount(new BigDecimal("3000000"));

        Invoice inv5 = new Invoice();
        inv5.setPaymentDate(today.atStartOfDay()); // Có ngày nhưng null tiền -> Phải bị loại, không được lỗi
        inv5.setTotalAmount(null);

        List<Invoice> mockInvoices = Arrays.asList(inv1, inv2, inv3, inv4, inv5);

        // --- Data Phòng ---
        Room room1 = new Room(); room1.setStatus("Trống");
        Room room2 = new Room(); room2.setStatus("Trống");
        Room room3 = new Room(); room3.setStatus("Đang sử dụng");
        Room room4 = new Room(); room4.setStatus("Dơ");

        List<Room> mockRooms = Arrays.asList(room1, room2, room3, room4);

        // 2. GIẢ LẬP HÀNH VI REPOSITORY
        when(invoiceRepository.findAll()).thenReturn(mockInvoices);
        when(roomRepository.findAll()).thenReturn(mockRooms);

        // 3. THỰC THI HÀM CẦN TEST
        Map<String, Object> stats = dashboardService.getDashboardStats();

        // 4. KIỂM TRA KẾT QUẢ (ASSERTIONS)
        assertNotNull(stats);

        // Kiểm tra Doanh thu (Chỉ cộng inv1 và inv2 = 1.500.000)
        BigDecimal expectedRevenue = new BigDecimal("1500000");
        assertEquals(expectedRevenue, stats.get("todayRevenue"), "Doanh thu hôm nay tính toán sai!");

        // Kiểm tra Tổng số phòng
        assertEquals(4, stats.get("totalRooms"), "Tổng số phòng đếm sai!");

        // Kiểm tra Thống kê trạng thái phòng
        @SuppressWarnings("unchecked")
        Map<String, Long> roomStatistics = (Map<String, Long>) stats.get("roomStatistics");

        assertNotNull(roomStatistics);
        assertEquals(2L, roomStatistics.get("Trống"), "Đếm số phòng Trống sai!");
        assertEquals(1L, roomStatistics.get("Đang sử dụng"), "Đếm số phòng Đang sử dụng sai!");
        assertEquals(1L, roomStatistics.get("Dơ"), "Đếm số phòng Dơ sai!");
    }
}