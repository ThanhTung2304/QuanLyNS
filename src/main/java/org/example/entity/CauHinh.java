package org.example.entity;

import java.util.Objects;

public class CauHinh {
    private String settingKey;
    private String settingValue;
    private String moTa;

    public CauHinh() {}

    public CauHinh(String settingKey, String settingValue, String moTa) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.moTa = moTa;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CauHinh cauHinh = (CauHinh) o;
        return Objects.equals(settingKey, cauHinh.settingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingKey);
    }
}