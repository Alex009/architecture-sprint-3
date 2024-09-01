package ru.yandex.practicum.homes.dto;

import lombok.Data;

@Data
public class AccessCheckRequestDTO {

    private String userId;
    private String homeId;
}
