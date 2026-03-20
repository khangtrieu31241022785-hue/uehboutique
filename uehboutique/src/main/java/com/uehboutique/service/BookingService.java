package com.uehboutique.service;

import com.uehboutique.entity.Booking;
import com.uehboutique.entity.Guest;
import com.uehboutique.entity.Room;
import com.uehboutique.entity.Staff;
import com.uehboutique.repository.BookingRepository;
import com.uehboutique.repository.GuestRepository;
import com.uehboutique.repository.RoomRepository;
import com.uehboutique.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final StaffRepository staffRepository;

    // Logic Check-in(US-02)
    @Transactional
    public Booking processCheckIn(Integer guessId, Integer roomId, Integer staffId, LocalDate checkOutDate) {
        // 1. Tìm thông tin Khách, Phòng, Nhân viên trong DB
        Guest guest = guestRepository.findById(guessId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // 2. KIỂM TRA LOGIC THỰC TẾ: Phòng có trống không?
        if(!room.getStatus().equalsIgnoreCase("Empty")){
            throw new RuntimeException("Error: Room " + room.getRoomNumber() + "not empty, can not Check-in");
        }

        // 3. Tạo phiếu Đặt phòng (Booking)
        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStaff(staff);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus("Check-in");

        // Change Status
        room.setStatus("Currently");
        roomRepository.save(room);// Cập nhật phòng vào Database

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking transferRoom(Integer bookingId, Integer roomId) {
        // 1. Tìm thông tin Booking hiện tại
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found" + bookingId));
        if("Check-out".equals(booking.getStatus())){
            throw new RuntimeException("Booking is already checked out, can not transfer room");
        }
        // 2. Tìm thông tin Phòng mới
        Room newRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if(!"Empty".equals(newRoom.getStatus())){
            throw new RuntimeException("New Room (" + newRoom.getRoomNumber() + ") is not empty, currently in the state: " + newRoom.getStatus());
        }
        // 3. Xử lý Phòng cũ: Đổi thành Dirty chờ dọn dẹp
        Room oldRoom = booking.getRoom();
        oldRoom.setStatus("Dirty");
        roomRepository.save(oldRoom);

        // 4. Xử lý Phòng mới: Đổi thành Currently (Đang ở)
        newRoom.setStatus("Currently");
        roomRepository.save(newRoom);

        // 5. Cập nhật lại phòng mới cho Booking
        booking.setRoom(newRoom);
        return bookingRepository.save(booking);
    }

    //    LUỒNG 2: ĐẶT PHÒNG TRƯỚC (RESERVATION)

    // 1. Khách gọi điện đặt giữ chỗ
    @Transactional
    public Booking reserveRoom(Integer guestId, Integer roomId, Integer staffId, LocalDate checkInDate, LocalDate checkOutDate) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Can not find guest with id: " + guestId));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Can not find room with id: " + roomId));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Can not find staff with id: " + staffId));

        // Kiểm tra phòng phải trống mới cho đặt
        if(!"Empty".equals(room.getStatus())){
            throw new RuntimeException("Room {" + room.getRoomNumber() + "} currently in the state " + room.getStatus() + ", can not reserve");
        }

        // Tạo Booking trạng thái Reserved
        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStaff(staff);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus("Reserved"); // Status: Đã đặt trước

        // Khóa phòng lại
        room.setStatus("Booked");
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    // 2. Khách đến nhận phòng đã đặt (Chuyển Reserved -> Check-in)
    @Transactional
    public Booking checkInReservedRoom(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Can not find booking with id: " + bookingId));
        if(!"Reserved".equals(booking.getStatus())){
            throw new RuntimeException("Booking {" + bookingId + "} currently in the state " + booking.getStatus() + ", can not check-in");
        }
        // Cập nhật trạng thái Booking và ngày đến thực tế
        booking.setStatus("Check-in");
        booking.setCheckInDate(LocalDate.now());

        // Cập nhật trạng thái phòng sang Đang sử dụng
        Room room = booking.getRoom();
        room.setStatus("Currently");
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    // 3. Khách hủy đặt phòng (Cancel)
    @Transactional
    public Booking cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Can not find booking with id: " + bookingId));

        if(!"Reserved".equals(booking.getStatus())){
            throw new RuntimeException("Booking {" + bookingId + "} currently in the state " + booking.getStatus() + ", can not cancel");
        }

        // Đổi trạng thái thành Hủy
        booking.setStatus("Canceled");

        // Nhả phòng lại cho khách khác thuê
        Room room = booking.getRoom();
        room.setStatus("Empty");
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }
}
