package top.criwits.sawa.model.aircraft;

import top.criwits.sawa.model.basic.AbstractFactory;

public interface AircraftFactory extends AbstractFactory {
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp);
}