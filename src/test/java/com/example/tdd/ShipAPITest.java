package com.example.tdd;

import com.example.tdd.model.ShipModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ShipAPITest {

  @InjectMocks
  private ShipApi shipApi;

  @Mock
  private Ship ship;

  MockMvc mvc;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(shipApi).build();
  }


  @Test
  public void shouldReturnNotFound404IfShipIdIsNotExist() throws Exception {

    String shipId = "1234";


    // Assumptions
    when(ship.findX(shipId)).thenReturn(empty());

    // Actual Code
    mvc.perform(get("/api/ship/"+shipId+"/position"))
        .andExpect(status().isNotFound());

    // verify
    verify(ship, times(1)).findX(shipId);
  }

  @Test
  public void shouldReturnTheShipXCoordinate() throws Exception {

    String shipId = "1234";

    Integer expectedXCoordinateOfTheShip = 1;


    // Assumptions
    when(ship.findX(shipId)).thenReturn(of(1));

    // Actual Code
    mvc.perform(get("/api/ship/"+shipId+"/position"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$",is(expectedXCoordinateOfTheShip)));

    // verify
    verify(ship, times(1)).findX(shipId);

  }


  @Test
  public void shouldReturnNotFoundIfSearchForNonExistedShip() throws Exception {
    String shipId = "1234";


    // Assumptions
    when(ship.find(shipId)).thenReturn(empty());

    // Actual Code
    mvc.perform(get("/api/ship/"+shipId))
        .andExpect(status().isNotFound());

    // verify
    verify(ship, times(1)).find(shipId);
  }

  @Test
  public void shouldReturnTheShipDetailsForExistingShip() throws Exception {
    String shipId = "1234";

    ShipModel expectedShip = ShipModel.builder()
        .id(shipId)
        .name("Nermandy 2")
        .xCoordinate(4)
        .yCoordinate(6)
        .visitedPlace(10)
        .visitedPlace(11)
        .build();

    // Assumptions
    when(ship.find(shipId)).thenReturn(of(expectedShip));

    // Actual Code
    mvc.perform(get("/api/ship/"+shipId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(expectedShip.getName())));

    // verify
    verify(ship, times(1)).find(shipId);

  }

  @Test
  public void shouldReturnCreatedIfAddNewShip() throws Exception {

    String shipId = "1234";

    ShipModel expectedShip = ShipModel.builder()
        .id(shipId)
        .name("Nermandy 2")
        .xCoordinate(4)
        .yCoordinate(6)
        .visitedPlace(10)
        .visitedPlace(11)
        .build();


    // Assumptions
    when(ship.add(expectedShip)).thenReturn(expectedShip);

    mvc.perform(post("/api/ship/")
                  .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                  .content(new ObjectMapper().writeValueAsString(expectedShip)))
        .andExpect(status().isCreated());

    // verify
    verify(ship, times(1)).add(expectedShip);
  }
}
