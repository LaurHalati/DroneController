package com.example.demo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
 interface DroneRepository extends ElasticsearchRepository<DronePlan,String> {
 @Override
List<DronePlan> findAll();
}