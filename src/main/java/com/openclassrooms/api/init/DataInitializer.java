package com.openclassrooms.api.init;

import com.openclassrooms.api.model.Voiture;
import com.openclassrooms.api.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createRandomVoitures(@Autowired VoitureRepository voitureRepository) {
        return (args) -> {
            voitureRepository.deleteAll();

            String[] colors = {"Rouge", "Bleu", "Vert", "Jaune", "Orange", "Blanc", "Noir", "Gris"};

            for (int i = 0; i < 100; i++) {
                String randomName = generateRandomName();
                String randomColor = generateRandomColor(colors);
                double randomPrice = generateRandomPrice();

                Voiture voiture = new Voiture(randomName, randomPrice, randomColor);
                voitureRepository.save(voiture);
            }
        };
    }

    private String generateRandomName() {
        Random random = new Random();
        int nameLength = 6 + random.nextInt(10 - 6 + 1);

        StringBuilder sb = new StringBuilder(nameLength);
        for (int i = 0; i < nameLength; i++) {
            char randomChar = (char) ('a' + random.nextInt(26));
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private double generateRandomPrice() {
        Random random = new Random();
        return (double) 2000 + ((double) 100000 - (double) 2000) * random.nextDouble();
    }

    private String generateRandomColor(String[] colors) {
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }
}
