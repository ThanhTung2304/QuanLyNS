package org.example.controller;

import org.example.entity.HopDong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.HopDongService;
import org.example.service.impl.HopDongServiceImpl;

import java.util.List;
import java.util.Optional;

public class HopDongController {

    private final HopDongService hopDongService;

    public HopDongController() {
        this.hopDongService = new HopDongServiceImpl();
    }

    public List<HopDong> getHopDongByMaNv(String maNv) {
        return hopDongService.getHopDongByMaNv(maNv);
    }

    public Optional<HopDong> getHopDongById(String maHd) {
        return hopDongService.getHopDongById(maHd);
    }

    public HopDong createHopDong(HopDong hopDong) throws ValidationException, BusinessException {
        return hopDongService.createHopDong(hopDong);
    }

    public HopDong updateHopDong(HopDong hopDong) throws ValidationException, BusinessException {
        return hopDongService.updateHopDong(hopDong);
    }

    public void deleteHopDong(String maHd) throws BusinessException {
        hopDongService.deleteHopDong(maHd);
    }
}