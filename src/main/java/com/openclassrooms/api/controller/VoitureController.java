package com.openclassrooms.api.controller;

import com.openclassrooms.api.model.Voiture;
import com.openclassrooms.api.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voitures")
public class VoitureController {

    @Autowired
    private VoitureRepository voitureRepository;

    @PostMapping
    public ResponseEntity<Voiture> createVoiture(@RequestBody Voiture voiture) {
        Voiture newVoiture = voitureRepository.save(voiture);
        return new ResponseEntity<>(newVoiture, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Voiture>> getAllVoitures(@RequestParam(value = "name", required = false) String name) {
        List<Voiture> voitures;

        if (name == null || name.isEmpty()) {
            voitures = voitureRepository.findAll();
        } else {
            voitures = voitureRepository.findByNameContainingIgnoreCase(name);
        }

        return new ResponseEntity<>(voitures, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voiture> getVoitureById(@PathVariable("id") Long id) {
        Optional<Voiture> voiture = voitureRepository.findById(id);
        return voiture.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voiture> updateVoiture(@PathVariable("id") Long id, @RequestBody Voiture voitureDetails) {
        Optional<Voiture> voitureOptional = voitureRepository.findById(id);

        if (voitureOptional.isPresent()) {
            Voiture voiture = voitureOptional.get();
            voiture.setName(voitureDetails.getName());
            voiture.setPrice(voitureDetails.getPrice());
            voiture.setColor(voitureDetails.getColor());
            Voiture updatedVoiture = voitureRepository.save(voiture);
            return new ResponseEntity<>(updatedVoiture, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoiture(@PathVariable("id") Long id) {
        Optional<Voiture> voitureOptional = voitureRepository.findById(id);

        if (voitureOptional.isPresent()) {
            voitureRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
