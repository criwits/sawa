package top.criwits.sawa.aircraft;

public class MobEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }
}