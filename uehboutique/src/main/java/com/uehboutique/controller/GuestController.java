package com.uehboutique.controller;

import com.uehboutique.entity.Guest;
import com.uehboutique.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Tự động tạo constructor cho Service
public class GuestController {

    // CHÚ Ý CHỖ NÀY: Giờ mình gọi Service chứ không gọi Repository nữa
    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
        return ResponseEntity.ok(guestService.saveGuest(guest));
    }

    @GetMapping
    public ResponseEntity<Page<Guest>> getAllGuests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(guestService.getAllGuests(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Integer id) {
        Optional<Guest> guest = guestService.getGuestById(id);
        if (guest.isPresent()) {
            return ResponseEntity.ok(guest.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Guest> getGuestByPhone(@RequestParam String phone) {
        Optional<Guest> guest = guestService.getGuestByPhone(phone);
        if (guest.isPresent()) {
            return ResponseEntity.ok(guest.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}