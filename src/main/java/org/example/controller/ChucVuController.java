package org.example.controller;

import org.example.entity.ChucVu;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.ChucVuService;
import org.example.service.impl.ChucVuServiceImpl;

import java.util.List;
import java.util.Optional;

public class ChucVuController {

    private final ChucVuService chucVuService;

    public ChucVuController() {
        this.chucVuService = new ChucVuServiceImpl();
    }

    public List<ChucVu> getAllChucVu() {
        return chucVuService.getAllChucVu();
    }

    public Optional<ChucVu> getChucVuById(String maChucVu) {
        return chucVuService.getChucVuById(maChucVu);
    }

    public ChucVu createChucVu(ChucVu chucVu) throws ValidationException, BusinessException {
        return chucVuService.createChucVu(chucVu);
    }

    public ChucVu updateChucVu(ChucVu chucVu) throws ValidationException, BusinessException {
        return chucVuService.updateChucVu(chucVu);
    }

    public void deleteChucVu(String maChucVu) throws BusinessException {
        chucVuService.deleteChucVu(maChucVu);
    }
}