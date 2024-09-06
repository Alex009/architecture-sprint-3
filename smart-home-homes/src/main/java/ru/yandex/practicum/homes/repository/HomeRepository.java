package ru.yandex.practicum.homes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.homes.model.Home;

import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, String> {

    List<Home> findByOwnerIdOrSharedUserIdsContains(String ownerId, String userId);
}
