package edu.sru.schedule.scheduleadvisement.repository.user;

import org.springframework.data.repository.CrudRepository;
import edu.sru.schedule.scheduleadvisement.domain.user.UserRole;

/**
 * Repository for accessing user role data.
 */
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByAuthorityName(String authorityName);
}

