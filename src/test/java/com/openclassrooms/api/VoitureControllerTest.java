package com.openclassrooms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.openclassrooms.api.repository.VoitureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.openclassrooms.api.model.Voiture;
import com.openclassrooms.api.controller.VoitureController;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(controllers = VoitureController.class)
public class VoitureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoitureRepository voitureRepository;

    @Test
    public void testGetVoitures() throws Exception {
        mockMvc.perform(get("/voitures"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVoituresByName() throws Exception {
        List<Voiture> audiVoitures = Arrays.asList(
                new Voiture("Audi A4", 15000.0, "red"),
                new Voiture("Audi A5", 20000.0, "blue")
        );

        when(voitureRepository.findByNameContainingIgnoreCase("Audi")).thenReturn(audiVoitures);

        mockMvc.perform(get("/voitures?name=Audi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Audi A4")))
                .andExpect(jsonPath("$[0].price", is(15000.0)))
                .andExpect(jsonPath("$[0].color", is("red")))
                .andExpect(jsonPath("$[1].name", is("Audi A5")))
                .andExpect(jsonPath("$[1].price", is(20000.0)))
                .andExpect(jsonPath("$[1].color", is("blue")));
    }

    @Test
    public void testGetVoitureByNameWithNoResult() throws Exception {
        when(voitureRepository.findByNameContainingIgnoreCase("Audi")).thenReturn(List.of());

        mockMvc.perform(get("/voitures?name=Audi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));
    }

    @Test
    public void testGetVoiture() throws Exception {
        when(voitureRepository.findById(1L)).thenReturn(Optional.of(new Voiture("Audi", 10000.0, "red")));

        mockMvc.perform(get("/voitures/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVoitureWithIncorrectParameter() throws Exception {
        mockMvc.perform(get("/voitures/notfound"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetVoitureWithIncorrectId() throws Exception {
        when(voitureRepository.findById(5000L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/voitures/5000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteVoiture() throws Exception {
        Voiture voiture = new Voiture("Audi", 10000.0, "red");
        when(voitureRepository.findById(1L)).thenReturn(Optional.of(voiture));
        doNothing().when(voitureRepository).deleteById(1L);

        mockMvc.perform(delete("/voitures/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateVoiture() throws Exception {
        Voiture existingVoiture = new Voiture("Audi", 10000.0, "red");
        Voiture updatedVoiture = new Voiture("BMW", 12000.0, "blue");

        when(voitureRepository.findById(1L)).thenReturn(Optional.of(existingVoiture));
        when(voitureRepository.save(any(Voiture.class))).thenReturn(updatedVoiture);

        mockMvc.perform(put("/voitures/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedVoiture)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.name", is("BMW")))
                .andExpect((ResultMatcher) jsonPath("$.price", is(12000.0)))
                .andExpect((ResultMatcher) jsonPath("$.color", is("blue")));
    }

    @Test
    public void testAddVoiture() throws Exception {
        Voiture newVoiture = new Voiture("BMW", 12000.0, "blue");

        when(voitureRepository.save(any(Voiture.class))).thenReturn(newVoiture);

        mockMvc.perform(post("/voitures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newVoiture)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.name", is("BMW")))
                .andExpect((ResultMatcher) jsonPath("$.price", is(12000.0)))
                .andExpect((ResultMatcher) jsonPath("$.color", is("blue")));
    }
}