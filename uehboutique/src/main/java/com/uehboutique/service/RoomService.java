package com.uehboutique.service;

import com.uehboutique.entity.Room;
import com.uehboutique.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getRoomsByStatus(String status) {
        return roomRepository.findByStatus(status);
    }

    // Thêm phòng mới (Mặc định phòng mới xây xong sẽ trống - Empty)
    public Room addRoom(Room room) {
        room.setStatus("Empty");
        return roomRepository.save(room);
    }


    @Transactional
    public Room cleanRoom(Integer roomId){
        // 1. Tìm phòng trong DB
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Can not find room with id: " + roomId));

        // 2. Kiểm tra xem phòng có đang "Dirty" không? (Phòng đang có khách thì không được vào dọn)
        if(!"Dirty".equals(room.getStatus())){
            throw new RuntimeException("Room {" + room.getRoomNumber() + "} currently in the state " + room.getStatus() + ", can not clean");
        }

        // 3. Dọn xong thì cập nhật thành Empty
        room.setStatus("Empty");
        return roomRepository.save(room);
    }
}
