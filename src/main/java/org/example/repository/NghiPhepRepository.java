package org.example.repository;

import org.example.entity.NghiPhep;
import java.util.List;
import java.util.Optional;

public interface NghiPhepRepository {
    List<NghiPhep> findAll();
    List<NghiPhep> findByMaNv(String maNv);
    Optional<NghiPhep> findById(Integer maNp);
    NghiPhep save(NghiPhep nghiPhep);
    boolean update(NghiPhep nghiPhep);
    boolean deleteById(Integer maNp);

    // --- Methods for Dashboard ---
    long countPendingRequests();
}