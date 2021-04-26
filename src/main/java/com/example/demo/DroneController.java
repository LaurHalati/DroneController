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
        //System.out.println(newDronePlan.getCoordinates());
//        List<List<List<Double>>> newCoordinates = new ArrayList<>();
//
//        newCoordinates.add(newDronePlan.getCoordinates());
//
//        newDronePlan.setGeometry(new Polygon(newCoordinates));
        System.out.println(newDronePlan);

//                try {
//
//            // Writing to a file
//            mapper.writeValue(new File("country.json"), newDronePlan.toString() );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //newDronePlan.setGeometry(new Polygon(newCoordinates));
       // System.out.println(newDronePlan.toString());
        //        double[][] d = newDronePlan.getCoordinates();
//        String[] parts = Arrays.deepToString(d).split(",");
//        parts[0] = parts[0].substring(2,parts[0].length());
//        parts[parts.length-1]= parts[parts.length-1].substring(0, parts[parts.length-1].length()-2);
//
//        for (int i=1;i<parts.length-2;i+=2) {
//                parts[i] = parts[i].substring(0, parts[i].length() - 1);
//                parts[i+1] = parts[i+1].substring(2);
//
//        }
//
//        double coordonate [][] = new double [parts.length/2][2];
//        for( int i=0;i<parts.length;i++){
//            int j=i/2;
//            if(i%2==0)
//                coordonate[j][0]= Double.parseDouble(parts[i]);
//            else
//                coordonate[j][1]= Double.parseDouble(parts[i]);
//        }
//        for (int i =0 ;i<coordonate.length;i++) {
//            for (int j = 0; j < 2; j++){
//                System.out.print(coordonate[i][j]+" ");
//        }
//            System.out.println(" ");
//
//        try {
//
//            // Writing to a file
//            mapper.writeValue(new File("C:/Users/halat/Desktop/Licenta/country.json"), newDronePlan );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        return repository.save(newDronePlan);
        //return newDronePlan;
    }
    @GetMapping("/drones/{id}")
    DronePlan one(@PathVariable String id) {

        return repository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException(id));
    }

//    @PutMapping("/drones/{id}")
//    DronePlan replaceDronePlan(@RequestBody DronePlan newDronePlan, @PathVariable String id) {
//
//        return repository.findById(id)
//                .map(dronePlan -> {
//                    dronePlan.setName(newDronePlan.getName());
//                    dronePlan.setGeometryType(newDronePlan.getGeometryType());
//                    return repository.save(dronePlan);
//                })
//                .orElseGet(() -> {
//                    newDronePlan.setId(id);
//                    return repository.save(newDronePlan);
//                });
//    }

    @DeleteMapping("/drones/{id}")
    void deleteEmployee(@PathVariable String id) {
        repository.deleteById(id);
    }
}

