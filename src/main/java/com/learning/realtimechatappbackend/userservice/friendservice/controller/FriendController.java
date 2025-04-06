package com.learning.realtimechatappbackend.userservice.friendservice.controller;

import com.learning.realtimechatappbackend.userservice.friendservice.dto.FriendRequestDto;
import com.learning.realtimechatappbackend.userservice.friendservice.dto.FriendshipDto;
import com.learning.realtimechatappbackend.userservice.friendservice.dto.UserSearchDto;
import com.learning.realtimechatappbackend.userservice.friendservice.service.FriendService;
import com.learning.realtimechatappbackend.userservice.userservice.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserSearchDto>> searchUserByEmail(
            @RequestParam String email,
            @RequestHeader("X-User-ID") String userId) {
        try {
            UserSearchDto user = friendService.searchUserByEmail(email, userId);
            return ResponseEntity.ok(ApiResponse.<UserSearchDto>builder()
                    .success(true)
                    .message("User found")
                    .data(user)
                    .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<UserSearchDto>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<UserSearchDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<FriendshipDto>> sendFriendRequest(
            @RequestBody FriendRequestDto friendRequestDto,
            @RequestHeader("X-User-ID") String userId) {
        try {
            FriendshipDto friendship = friendService.sendFriendRequest(userId, friendRequestDto);
            return ResponseEntity.ok(ApiResponse.<FriendshipDto>builder()
                    .success(true)
                    .message("Friend request sent")
                    .data(friendship)
                    .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/request/{friendshipId}/accept")
    public ResponseEntity<ApiResponse<FriendshipDto>> acceptFriendRequest(
            @PathVariable String friendshipId,
            @RequestHeader("X-User-ID") String userId) {
        try {
            FriendshipDto friendship = friendService.respondToFriendRequest(userId, friendshipId, true);
            return ResponseEntity.ok(ApiResponse.<FriendshipDto>builder()
                    .success(true)
                    .message("Friend request accepted")
                    .data(friendship)
                    .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/request/{friendshipId}/reject")
    public ResponseEntity<ApiResponse<FriendshipDto>> rejectFriendRequest(
            @PathVariable String friendshipId,
            @RequestHeader("X-User-ID") String userId) {
        try {
            FriendshipDto friendship = friendService.respondToFriendRequest(userId, friendshipId, false);
            return ResponseEntity.ok(ApiResponse.<FriendshipDto>builder()
                    .success(true)
                    .message("Friend request rejected")
                    .data(friendship)
                    .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<FriendshipDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<FriendshipDto>>> getPendingRequests(
            @RequestHeader("X-User-ID") String userId) {
        try {
            List<FriendshipDto> pendingRequests = friendService.getPendingRequests(userId);
            return ResponseEntity.ok(ApiResponse.<List<FriendshipDto>>builder()
                    .success(true)
                    .message("Pending friend requests retrieved")
                    .data(pendingRequests)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<FriendshipDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSearchDto>>> getFriends(
            @RequestHeader("X-User-ID") String userId) {
        try {
            List<UserSearchDto> friends = friendService.getFriends(userId);
            return ResponseEntity.ok(ApiResponse.<List<UserSearchDto>>builder()
                    .success(true)
                    .message("Friends retrieved")
                    .data(friends)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<UserSearchDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
}
