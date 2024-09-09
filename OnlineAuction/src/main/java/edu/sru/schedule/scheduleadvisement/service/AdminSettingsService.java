package edu.sru.schedule.scheduleadvisement.service;

import edu.sru.schedule.scheduleadvisement.domain.AdminSettings;
import edu.sru.schedule.scheduleadvisement.repositories.AdminSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to admin settings within the application.
 * Provides methods to retrieve and save admin settings.
 */
@Service
public class AdminSettingsService {

	private final AdminSettingsRepository settingsRepository;

	@Autowired
	public AdminSettingsService(AdminSettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}

	/**
     * Retrieves the AdminSettings entity with a specific ID 1 from the database.
     * If the settings are not found, returns null.
     *
     * @return the AdminSettings entity if found, otherwise null.
     */
	public AdminSettings getSettings() {
		return settingsRepository.findById(1L).orElse(null);
	}

	/**
     * Saves AdminSettings to the database.
     *
     * @param settings the AdminSettings entity to be saved.
     * @return the saved AdminSettings entity.
     */
	public AdminSettings saveSettings(AdminSettings settings) {
		return settingsRepository.save(settings);
	}
}
