package edu.sru.schedule.scheduleadvisement.service;

import edu.sru.schedule.scheduleadvisement.domain.AdminSettings;
import edu.sru.schedule.scheduleadvisement.repositories.AdminSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminSettingsServiceTest {

    @Mock
    private AdminSettingsRepository settingsRepository;

    @InjectMocks
    private AdminSettingsService adminSettingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSettingsTest() {
        AdminSettings expectedSettings = new AdminSettings();
        when(settingsRepository.findById(1L)).thenReturn(java.util.Optional.of(expectedSettings));

        AdminSettings actualSettings = adminSettingsService.getSettings();

        assertEquals(expectedSettings, actualSettings, "The settings should match the expected settings.");
    }

    @Test
    void saveSettingsTest() {
        AdminSettings settingsToSave = new AdminSettings();
        when(settingsRepository.save(settingsToSave)).thenReturn(settingsToSave);

        AdminSettings savedSettings = adminSettingsService.saveSettings(settingsToSave);

        assertNotNull(savedSettings, "The saved settings should not be null.");
        assertEquals(settingsToSave, savedSettings, "The saved settings should match the settings to save.");
    }
}
