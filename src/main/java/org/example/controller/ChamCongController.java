package org.example.controller;

import org.example.entity.ChamCong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.ChamCongService;
import org.example.service.impl.ChamCongServiceImpl;

import java.util.List;
import java.util.Optional;

public class ChamCongController {

    private final ChamCongService chamCongService;

    public ChamCongController() {
        this.chamCongService = new ChamCongServiceImpl();
    }

    public List<ChamCong> getChamCongByMaNv(String maNv) {
        return chamCongService.getChamCongByMaNv(maNv);
    }

    public Optional<ChamCong> getChamCongById(Integer maCc) {
        return chamCongService.getChamCongById(maCc);
    }

    public ChamCong createChamCong(ChamCong chamCong) throws ValidationException, BusinessException {
        return chamCongService.createChamCong(chamCong);
    }

    public ChamCong updateChamCong(ChamCong chamCong) throws ValidationException, BusinessException {
        return chamCongService.updateChamCong(chamCong);
    }

    public void deleteChamCong(Integer maCc) throws BusinessException {
        chamCongService.deleteChamCong(maCc);
    }

    public ChamCong checkIn(String maNv) throws BusinessException {
        return chamCongService.checkIn(maNv);
    }

    public ChamCong checkOut(String maNv) throws BusinessException {
        return chamCongService.checkOut(maNv);
    }

    public Optional<ChamCong> getTodayChamCong(String maNv) {
        return chamCongService.getTodayChamCong(maNv);
    }
}