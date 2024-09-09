package edu.sru.schedule.scheduleadvisement.repositories;

import org.springframework.data.repository.CrudRepository;
import edu.sru.schedule.scheduleadvisement.domain.AdvisorData;

/**
 * repository interface for Advisors.
 * Provides CRUD operations from CrudRepository.
 */
public interface AdvisorRepository extends CrudRepository<AdvisorData, Long> 
{
	boolean existsByEmail(String email);	
}
