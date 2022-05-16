package top.criwits.sawa.prop;

public class BulletPropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new BulletProp(locationX, locationY, speedX, speedY);
    }
}
