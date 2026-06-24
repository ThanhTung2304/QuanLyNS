package org.example.service;

import org.example.entity.HopDong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface HopDongService {
    List<HopDong> getHopDongByMaNv(String maNv);
    Optional<HopDong> getHopDongById(String maHd);
    HopDong createHopDong(HopDong hopDong) throws ValidationException, BusinessException;
    HopDong updateHopDong(HopDong hopDong) throws ValidationException, BusinessException;
    void deleteHopDong(String maHd) throws BusinessException;
}