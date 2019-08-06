package com.piggymetrics.statistics.repository;

import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DataPointRepository extends JpaRepository<DataPoint, Long> {

	@Transactional(timeout = 10)
	@Modifying(clearAutomatically=true)
	@Query("select dp from DataPoint dp where dp.account = :account")
	List<DataPoint> getDataPointList(@Param("account") String account);

}
