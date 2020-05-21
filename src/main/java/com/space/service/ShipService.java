package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ShipService {
    private ShipRepo repo;

    public ShipService(ShipRepo repo) {
        this.repo = repo;
    }

    public List<Ship> getShips(String name,
                               String planet,
                               ShipType shipType,
                               Long after,
                               Long before,
                               Boolean isUsed,
                               Double minSpeed,
                               Double maxSpeed,
                               Integer minCrewSize,
                               Integer maxCrewSize,
                               Double minRating,
                               Double maxRating,
                               ShipOrder order,
                               Integer pageNumber,
                               Integer pageSize) {

        List<Ship> ships = listOfShips(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        List<Ship> sortedShips = sortedListOfShips(ships, order);

        if (pageNumber == null)
            pageNumber = 0;

        if (pageSize == null)
            pageSize = 3;

        int maxCount = pageSize * pageNumber + pageSize;
        if (maxCount > sortedShips.size())
            maxCount = sortedShips.size();

        List<Ship> result = new ArrayList<>(pageSize);

        for (int i = pageSize * pageNumber; i < maxCount; i++) {
            result.add(sortedShips.get(i));
        }

        return result;

    }

    public Integer getShipsCount(String name,
                                 String planet,
                                 ShipType shipType,
                                 Long after,
                                 Long before,
                                 Boolean isUsed,
                                 Double minSpeed,
                                 Double maxSpeed,
                                 Integer minCrewSize,
                                 Integer maxCrewSize,
                                 Double minRating,
                                 Double maxRating) {

        List<Ship> ships = listOfShips(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return ships.size();
    }

    private List<Ship> sortedListOfShips(List<Ship> ships, ShipOrder order) {
        if (order == null || order == ShipOrder.ID) {
            ships.sort(new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        }

        if (order == ShipOrder.SPEED) {
            ships.sort(new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    Double o1Speed = o1.getSpeed() == null ? 0 : o1.getSpeed();
                    Double o2Speed = o2.getSpeed() == null ? 0 : o2.getSpeed();
                    return o1Speed.compareTo(o2Speed);
                }
            });
        }

        if (order == ShipOrder.DATE) {
            ships.sort(new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    Date o1Date = o1.getProdDate() == null ? new Date(Long.MIN_VALUE) : o1.getProdDate();
                    Date o2Date = o2.getProdDate() == null ? new Date(Long.MIN_VALUE) : o2.getProdDate();
                    return o1Date.compareTo(o2Date);
                }
            });
        }

        if (order == ShipOrder.RATING) {
            ships.sort(new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    Double o1Ratting = o1.getRating() == null ? 0 : o1.getRating();
                    Double o2Ratting = o2.getRating() == null ? 0 : o2.getRating();
                    return o1Ratting.compareTo(o2Ratting);
                }
            });
        }

        return ships;
    }

    private List<Ship> listOfShips(String name,
                                   String planet,
                                   ShipType shipType,
                                   Long after,
                                   Long before,
                                   Boolean isUsed,
                                   Double minSpeed,
                                   Double maxSpeed,
                                   Integer minCrewSize,
                                   Integer maxCrewSize,
                                   Double minRating,
                                   Double maxRating
    ) {

        if (name != null) {
            name = name.toLowerCase();
        }

        if (planet != null) {
            planet = planet.toLowerCase();
        }

        List<Ship> res = new ArrayList<>();
        Iterable<Ship> allShips = repo.findAll();

        for (Ship ship : allShips) {
            if (name != null) {
                String shipName = ship.getName();
                if (shipName == null || !shipName.toLowerCase().contains(name))
                    continue;
            }

            if (planet != null) {
                String shipPlanet = ship.getPlanet();
                if (shipPlanet == null || !shipPlanet.toLowerCase().contains(planet))
                    continue;
            }

            if (shipType != null) {
                if (ship.getShipType() != shipType)
                    continue;
            }

            if (after != null) {
                if (ship.getProdDate() == null || ship.getProdDate().getTime() < after)
                    continue;
            }

            if (before != null) {
                if (ship.getProdDate() == null || ship.getProdDate().getTime() > before)
                    continue;
            }

            if (isUsed != null) {
                if (ship.getUsed() == null || !ship.getUsed().equals(isUsed))
                    continue;
            }

            if (minSpeed != null) {
                if (ship.getSpeed() == null || ship.getSpeed() < minSpeed)
                    continue;
            }

            if (maxSpeed != null) {
                if (ship.getSpeed() == null || ship.getSpeed() > maxSpeed)
                    continue;
            }

            if (minCrewSize != null) {
                if (ship.getCrewSize() == null || ship.getCrewSize() < minCrewSize)
                    continue;
            }

            if (maxCrewSize != null) {
                if (ship.getCrewSize() == null || ship.getCrewSize() > maxCrewSize)
                    continue;
            }

            if (minRating != null) {
                if (ship.getRating() == null || ship.getRating() < minRating)
                    continue;
            }

            if (maxRating != null) {
                if (ship.getRating() == null || ship.getRating() > maxRating)
                    continue;
            }

            res.add(ship);

        }
        return res;

    }


    public Ship getShipById(String id) {
        Long idL = validateAndGetId(id);
        Optional<Ship> opt = repo.findById(idL);

        if (opt.isPresent())
            return opt.get();
        else
            throw new NotFoundException();
    }

    private Long validateAndGetId(String id) {
        Long idL;

        try {
            idL = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new BadRequestException();
        }

        if (idL <= 0)
            throw new BadRequestException();

        return idL;
    }

    public Ship createShip(Ship newShip) {
        validateName(newShip.getName());
        validateName(newShip.getPlanet());

        validateProdDate(newShip.getProdDate());

        validateSpeed(newShip.getSpeed());
        validateCrewSize(newShip.getCrewSize());
        validateShipType(newShip.getShipType());

        if (newShip.getUsed() == null)
            newShip.setUsed(false);

        newShip.setRating(calculateRatting(newShip));

        return repo.save(newShip);
    }

    private double calculateRatting(Ship ship) {
        double k = ship.getUsed() ? 0.5 : 1;
        int y = ship.getProdDate().getYear() + 1900;

        double rating = (80 * ship.getSpeed() * k) / (3019 - y + 1);
        rating = Math.round(rating * 100) / 100.0;

        return rating;
    }

    private void validateShipType(ShipType shipType) {
        if (shipType == null)
            throw new BadRequestException();
    }

    private void validateCrewSize(Integer crewSize) {
        if (crewSize == null || crewSize < 1 || crewSize > 9999)
            throw new BadRequestException();
    }

    private void validateSpeed(Double speed) {
        if (speed == null || speed < 0.05 - 0.005 || speed > 0.99 + 0.00499)
            throw new BadRequestException();
    }

    private void validateProdDate(Date prodDate) {
        if (prodDate == null)
            throw new BadRequestException();

        Date after = new Date(1119, Calendar.DECEMBER, 31, 23, 59, 59);
        Date before = new Date(900, Calendar.JANUARY, 1, 0, 0, 0);

        if (prodDate.getTime() < before.getTime() || prodDate.getTime() > after.getTime())
            throw new BadRequestException();
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty() || name.length() > 50)
            throw new BadRequestException();

    }


    public Ship update(String id, Ship update) {
        Ship ship = getShipById(id);

        if (update.getName() != null) {
            validateName(update.getName());
            ship.setName(update.getName());
        }

        if (update.getPlanet() != null) {
            validateName(update.getPlanet());
            ship.setPlanet(update.getPlanet());
        }

        if (update.getShipType() != null) {
            validateShipType(update.getShipType());
            ship.setShipType(update.getShipType());
        }

        if (update.getProdDate() != null) {
            validateProdDate(update.getProdDate());
            ship.setProdDate(update.getProdDate());
        }

        if (update.getUsed() != null) {
            ship.setUsed(update.getUsed());
        }

        if (update.getSpeed() != null) {
            validateSpeed(update.getSpeed());
            ship.setSpeed(update.getSpeed());
        }

        if (update.getCrewSize() != null) {
            validateCrewSize(update.getCrewSize());
            ship.setCrewSize(update.getCrewSize());
        }

        ship.setRating(calculateRatting(ship));

        return repo.save(ship);

    }

    public Ship delete(String id) {
        repo.delete(getShipById(id));
        return null;
    }
}
