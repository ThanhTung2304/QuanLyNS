package org.example.repository;

import org.example.entity.CauHinh;
import java.util.Optional;

public interface CauHinhRepository {
    Optional<CauHinh> findByKey(String key);
    boolean saveOrUpdate(CauHinh cauHinh);
}