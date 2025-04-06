package com.learning.realtimechatappbackend.userservice.friendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private String addresseeId;
}
