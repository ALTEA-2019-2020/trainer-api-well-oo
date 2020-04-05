package com.miage.altea.trainer_api.controller;

import com.miage.altea.trainer_api.bo.Pokemon;
import com.miage.altea.trainer_api.bo.Trainer;
import com.miage.altea.trainer_api.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TrainerController controller;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Autowired
    private TrainerRepository trainerRepository;

    @BeforeEach
    void init() {
        trainerRepository.deleteAll();

        var ash = new Trainer("Ash");
        var pikachu = new Pokemon(25, 18);
        ash.setTeam(List.of(pikachu));

        var misty = new Trainer("Misty");
        var staryu = new Pokemon(120, 18);
        var starmie = new Pokemon(121, 21);
        misty.setTeam(List.of(staryu, starmie));

        // save a couple of trainers
        trainerRepository.save(ash);
        trainerRepository.save(misty);
    }

    @Test
    void trainerController_shouldBeInstanciated(){
        assertNotNull(controller);
    }

    @Test
    void getTrainers_shouldThrowAnUnauthorized(){
        var responseEntity = this.restTemplate
                .getForEntity("http://localhost:" + port + "/trainers/Ash", Trainer.class);
        assertNotNull(responseEntity);
        assertEquals(401, responseEntity.getStatusCodeValue());
    }

    @Test
    void getTrainer_withNameAsh_shouldReturnAsh() {
        var ash = this.restTemplate
                .withBasicAuth(username, password)
                .getForObject("http://localhost:" + port + "/trainers/Ash", Trainer.class);
        assertNotNull(ash);
        assertEquals("Ash", ash.getName());
        assertEquals(1, ash.getTeam().size());

        assertEquals(25, ash.getTeam().get(0).getPokemonTypeId());
        assertEquals(18, ash.getTeam().get(0).getLevel());
    }

    @Test
    void getAllTrainers_shouldReturnAshAndMisty() {
        var trainers = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainers);
        assertEquals(2, trainers.length);

        assertEquals("Ash", trainers[0].getName());
        assertEquals("Misty", trainers[1].getName());
    }


    @Test
    void createTrainers_shouldCreateNewEntryInDatabase() {
        var trainersBefore = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);


        Trainer newTrainer = new Trainer("Aya");
        newTrainer.setTeam(Arrays.asList(new Pokemon(1,1)));
        var trainers = this.restTemplate.withBasicAuth(username, password).postForObject("http://localhost:" + port + "/trainers/", newTrainer, Trainer.class);
        assertNotNull(trainers);
        assertEquals("Aya", trainers.getName());

        var trainersAfter = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersAfter);
        assertEquals(3, trainersAfter.length);
    }

    @Test
    void updateTrainers_shouldUpdateEntryInDatabase() {
        var trainersBefore = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);
        assertEquals("Ash", trainersBefore[0].getName());
        assertEquals(18, trainersBefore[0].getTeam().get(0).getLevel());
        assertEquals("Misty", trainersBefore[1].getName());

        Trainer trainer = trainersBefore[0];
        trainer.getTeam().get(0).setLevel(17);

        this.restTemplate.withBasicAuth(username, password).put("http://localhost:" + port + "/trainers/" + trainer.getName(), trainer, Trainer.class);


        var trainersAfter = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersAfter);
        assertEquals(2, trainersAfter.length);
        assertEquals("Ash", trainersAfter[0].getName());
        assertEquals(17, trainersAfter[0].getTeam().get(0).getLevel());
        assertEquals("Misty", trainersAfter[1].getName());

    }

    @Test
    void deleteTrainer_shouldDeleteEntryInDatabase() {
        var trainersBefore = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);
        assertEquals("Ash", trainersBefore[0].getName());
        assertEquals("Misty", trainersBefore[1].getName());


        this.restTemplate.withBasicAuth(username, password).delete("http://localhost:" + port + "/trainers/"+ trainersBefore[0].getName(), Trainer.class);


        var trainersAfter = this.restTemplate.withBasicAuth(username, password).getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersAfter);
        assertEquals(1, trainersAfter.length);
        assertEquals("Misty", trainersAfter[0].getName());

    }


}
