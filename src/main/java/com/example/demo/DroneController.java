package com.example.demo;



import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping
public class DroneController {

    private final DroneRepository repository;

    DroneController(DroneRepository repository){
        this.repository = repository;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/drones")

    List<DronePlan>  all(){

        return  repository.findAll();
    }

    @CrossOrigin(origins = "*")
    @PostMapping( value ="/drones",consumes = "application/json")
    DronePlan  newDronePlan (@RequestBody DronePlan newDronePlan) {
        System.out.println(newDronePlan);
        return repository.save(newDronePlan);
        //return newDronePlan;
    }
    @GetMapping("/drones/{id}")
    DronePlan one(@PathVariable String id) {

        return repository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
    }

    @DeleteMapping("/drones/{id}")
    void deleteEmployee(@PathVariable String id) {
        repository.deleteById(id);
    }
}

