package edu.sru.schedule.scheduleadvisement.repositories;

import edu.sru.schedule.scheduleadvisement.domain.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;

//repository for admin settings 
public interface AdminSettingsRepository extends JpaRepository<AdminSettings, Long> {

}
