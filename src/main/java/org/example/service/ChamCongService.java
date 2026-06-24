package org.example.service;

import org.example.entity.ChamCong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface ChamCongService {
    List<ChamCong> getChamCongByMaNv(String maNv);
    Optional<ChamCong> getChamCongById(Integer maCc);
    ChamCong createChamCong(ChamCong chamCong) throws ValidationException, BusinessException;
    ChamCong updateChamCong(ChamCong chamCong) throws ValidationException, BusinessException;
    void deleteChamCong(Integer maCc) throws BusinessException;

    // Các phương thức mới cho check-in/check-out
    ChamCong checkIn(String maNv) throws BusinessException;
    ChamCong checkOut(String maNv) throws BusinessException;
    Optional<ChamCong> getTodayChamCong(String maNv);
}