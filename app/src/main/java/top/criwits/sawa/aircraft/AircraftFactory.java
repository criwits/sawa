package top.criwits.sawa.aircraft;

import top.criwits.sawa.basic.AbstractFactory;

public interface AircraftFactory extends AbstractFactory {
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp);
}