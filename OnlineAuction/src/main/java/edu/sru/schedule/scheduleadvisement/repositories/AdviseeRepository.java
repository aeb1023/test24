package edu.sru.schedule.scheduleadvisement.repositories;

import org.springframework.data.repository.CrudRepository;
import edu.sru.schedule.scheduleadvisement.domain.AdviseeData;
import java.util.List;
import java.util.Optional;

/**
 * repository interface for managing AdviseeData entities.
 * Provides CRUD operations and additional queries to handle specific searches involving advisee details.
 */
public interface AdviseeRepository extends CrudRepository<AdviseeData, Long> {
    // find advisees by their advisor 
	List<AdviseeData> findByAdvisorId(Long advisorId);
	
	//find by email 
	Optional<AdviseeData> findByEmail(String email);

	// find an AdviseeData by email and advisorId
    AdviseeData findByEmailAndAdvisorId(String email, Long advisorId);
}
