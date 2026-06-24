package org.example.controller;

import org.example.dto.DashboardStats;
import org.example.service.DashboardService;
import org.example.service.impl.DashboardServiceImpl;

public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController() {
        this.dashboardService = new DashboardServiceImpl();
    }

    public DashboardStats getDashboardStats() {
        return dashboardService.getDashboardStats();
    }
}