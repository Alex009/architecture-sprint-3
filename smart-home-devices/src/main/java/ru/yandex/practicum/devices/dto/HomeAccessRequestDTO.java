package ru.yandex.practicum.devices.dto;

import lombok.Data;

@Data
public class HomeAccessRequestDTO {

    private String userId;
    private String homeId;
}
