package top.criwits.sawa.aircraft;

public class EliteEnemyFactory implements AircraftFactory{
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
