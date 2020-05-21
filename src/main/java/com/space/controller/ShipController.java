package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rest/ships")
public class ShipController {
    private ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> getShips(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String planet,
                               @RequestParam(required = false) ShipType shipType,
                               @RequestParam(required = false) Long after,
                               @RequestParam(required = false) Long before,
                               @RequestParam(required = false) Boolean isUsed,
                               @RequestParam(required = false) Double minSpeed,
                               @RequestParam(required = false) Double maxSpeed,
                               @RequestParam(required = false) Integer minCrewSize,
                               @RequestParam(required = false) Integer maxCrewSize,
                               @RequestParam(required = false) Double minRating,
                               @RequestParam(required = false) Double maxRating,
                               @RequestParam(required = false) ShipOrder order,
                               @RequestParam(required = false) Integer pageNumber,
                               @RequestParam(required = false) Integer pageSize) {
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber,
                pageSize);
    }

    @GetMapping("/count")
    public Integer getShipCount(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String planet,
                                @RequestParam(required = false) ShipType shipType,
                                @RequestParam(required = false) Long after,
                                @RequestParam(required = false) Long before,
                                @RequestParam(required = false) Boolean isUsed,
                                @RequestParam(required = false) Double minSpeed,
                                @RequestParam(required = false) Double maxSpeed,
                                @RequestParam(required = false) Integer minCrewSize,
                                @RequestParam(required = false) Integer maxCrewSize,
                                @RequestParam(required = false) Double minRating,
                                @RequestParam(required = false) Double maxRating) {
        return shipService.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
    }

    @GetMapping("{id}")
    public Ship getShipById(@PathVariable String id) {
        return shipService.getShipById(id);
    }

    @PostMapping
    public Ship create(@RequestBody Ship newShip) {

        return shipService.createShip(newShip);

    }

    @PostMapping("{id}")
    public Ship update(@PathVariable String id,
                       @RequestBody Ship update) {
        return shipService.update(id, update);
    }

    @DeleteMapping("{id}")
    public Ship delete(@PathVariable String id){
        return shipService.delete(id);
    }

}
