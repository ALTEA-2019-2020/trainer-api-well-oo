package com.miage.altea.trainer_api.controller;

import com.miage.altea.trainer_api.bo.Pokemon;
import com.miage.altea.trainer_api.bo.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;

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

    @Test
    void trainerController_shouldBeInstanciated(){
        assertNotNull(controller);
    }

    @Test
    void getTrainer_withNameAsh_shouldReturnAsh() {
        var ash = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/Ash", Trainer.class);
        assertNotNull(ash);
        assertEquals("Ash", ash.getName());
        assertEquals(1, ash.getTeam().size());

        assertEquals(25, ash.getTeam().get(0).getPokemonTypeId());
        assertEquals(18, ash.getTeam().get(0).getLevel());
    }

    @Test
    void getAllTrainers_shouldReturnAshAndMisty() {
        var trainers = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainers);
        assertEquals(2, trainers.length);

        assertEquals("Ash", trainers[0].getName());
        assertEquals("Misty", trainers[1].getName());
    }


    @Test
    void createTrainers_shouldCreateNewEntryInDatabase() {
        var trainersBefore = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);


        Trainer newTrainer = new Trainer("Aya");
        newTrainer.setTeam(Arrays.asList(new Pokemon(1,1)));
        var trainers = this.restTemplate.postForObject("http://localhost:" + port + "/trainers/", newTrainer, Trainer.class);
        assertNotNull(trainers);
        assertEquals("Aya", trainers.getName());

        var trainersAfter = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersAfter);
        assertEquals(3, trainersAfter.length);
    }

    @Test
    void updateTrainers_shouldUpdateEntryInDatabase() {
        var trainersBefore = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);
        assertEquals("Ash", trainersBefore[0].getName());
        assertEquals(18, trainersBefore[0].getTeam().get(0).getLevel());
        assertEquals("Misty", trainersBefore[1].getName());

        Trainer trainer = trainersBefore[0];
        trainer.getTeam().get(0).setLevel(17);

        this.restTemplate.put("http://localhost:" + port + "/trainers/" + trainer.getName(), trainer, Trainer.class);


        var trainersAfter = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersAfter.length);
        assertEquals("Ash", trainersAfter[0].getName());
        assertEquals(17, trainersAfter[0].getTeam().get(0).getLevel());
        assertEquals("Misty", trainersAfter[1].getName());

    }

    @Test
    void deleteTrainer_shouldDeleteEntryInDatabase() {
        var trainersBefore = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(2, trainersBefore.length);
        assertEquals("Ash", trainersBefore[0].getName());
        assertEquals("Misty", trainersBefore[1].getName());


        this.restTemplate.delete("http://localhost:" + port + "/trainers/"+ trainersBefore[0].getName(), Trainer.class);


        var trainersAfter = this.restTemplate.getForObject("http://localhost:" + port + "/trainers/", Trainer[].class);
        assertNotNull(trainersBefore);
        assertEquals(1, trainersAfter.length);
        assertEquals("Misty", trainersAfter[0].getName());

    }


}
