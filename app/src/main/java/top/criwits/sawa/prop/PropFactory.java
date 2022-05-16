package top.criwits.sawa.prop;

import top.criwits.sawa.basic.AbstractFactory;

public interface PropFactory extends AbstractFactory {
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY);
}
