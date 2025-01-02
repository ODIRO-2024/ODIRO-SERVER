package odiro.repository;

import odiro.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import odiro.domain.Plan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByIdIn(List<Long> ids);
    List<Plan> findPlanByPlanFilter(String filterNum);
}
