package top.criwits.sawa.prop;

public class BombPropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new BombProp(locationX, locationY, speedX, speedY);
    }
}
